package game.app.service.redis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;
import game.app.content.service.ContentCacheManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Listener subscribes to CONTENT_CHANGE topic and invalidates the content cache manager when it receives messages.
 * 
 * @author m.mcbride
 *
 */
@Component
public class ContentChangeListener extends JedisPubSub
{
	
	public static final String CONTENT_CHANGE = "CONTENT_CHANGE";
	public static final String LOCALIZATION_CHANGE = "LOCALIZATION_CHANGE";
	public static final String INVALIDATE_COMMAND = "INVALIDATE";
	
	private static final Logger LOGGER = LoggerFactory.getLogger( ContentChangeListener.class );
	
	@Inject
	private JedisPool jedisPool;
	
	@Inject
	private ContentCacheManager contentCacheManager;
	

	
	private ExecutorService executor;
	
	@Value("${redis.subscription.recovery.interval:5000}")
	private Integer recoveryInterval;
	
	private volatile boolean running = true;
	
	@PostConstruct
	public void subscribe()
	{
		executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				return new Thread(r, "Content Change Subscriber");
			}
		});
		executor.execute(new Runnable() {
            public void run() {
            	while (running){
            		Jedis jedis = null;
            		try {
            			LOGGER.info("Subscribing to content change channel.");
    					jedis = jedisPool.getResource();
    					jedis.subscribe(ContentChangeListener.this, CONTENT_CHANGE, LOCALIZATION_CHANGE);
    				} catch (JedisConnectionException e) {
    					LOGGER.error("We've lost our subscription to content changes.", e);
    					if ( jedis != null ) {
    						jedisPool.returnBrokenResource(jedis);
    						jedis = null;
    					}
    					sleepBeforeRecoveryAttempt();
    				} finally {
    					if ( jedis != null ) {
    						LOGGER.info("Closing content change channel connection.");
    						jedisPool.returnResource(jedis);
    						jedis = null;
    					}
    				}
            	}
        		
            }
        });
	}
	
	protected void sleepBeforeRecoveryAttempt() {
		if (this.recoveryInterval > 0) {
			try {
				LOGGER.info("Sleeping for {} before trying to connect again.", this.recoveryInterval);
				Thread.sleep(this.recoveryInterval);
			}
			catch (InterruptedException interEx) {
				LOGGER.debug("Thread interrupted while sleeping the recovery interval");
			}
		}
	}
	
	@PreDestroy
	public void stopThread()
	{
		running = false;
		this.unsubscribe();
		LOGGER.info("Stopping subscriber.");
		executor.shutdownNow();
	}

	@Override
	public void onMessage(String channel, String message)
	{
		LOGGER.info("Received message: {} on channel: {}", message, channel);
		if (INVALIDATE_COMMAND.equalsIgnoreCase(message) && CONTENT_CHANGE.equalsIgnoreCase(channel)){
			LOGGER.info("Invalidating the cache.");
			contentCacheManager.invalidateAll();

		} else if (INVALIDATE_COMMAND.equalsIgnoreCase(message) && LOCALIZATION_CHANGE.equalsIgnoreCase(channel)){
			LOGGER.info("Invalidating the localization cache.");
		//contentCacheManager.invalidateMetadataCache();
			//contentCacheManager.invalidateLocalizationCaches();
		}
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) { }

	@Override
	public void onSubscribe(String channel, int subscribedChannels) { }

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) { }

	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) { }

	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) { }

}
