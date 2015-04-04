package game.framework.dao.redis;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.inject.Inject;

/**
 * Template class that removes the need to fetch and return a jedis connection from the pool.
 *
 * @author m.mcbride
 */

@SuppressWarnings( "SpellCheckingInspection" )
@Component
public class RedisTemplate {

	private static final Logger LOGGER = LoggerFactory.getLogger( RedisTemplate.class );

	@Inject
	private JedisPool jedisPool;

	public <T> T execute( RedisCallback<T> callback ) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return callback.execute( jedis );
		} catch ( JedisConnectionException e ) {
			LOGGER.warn( "Encountered connection exception using redis, removing resource from pool.", e );
			// https://github.com/xetorthio/jedis/issues/176
			if ( jedis != null ) {
				jedisPool.returnBrokenResource( jedis );
				jedis = null;
			}
			// wrap in a coded exception
			throw new RedisException( e );
		} finally {
			if ( jedis != null ) jedisPool.returnResource( jedis );
		}
	}
}
