package com.ares.app.content.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.stereotype.Component;

import com.ares.app.content.vip.VipConfig;
import com.ares.app.content.vip.VipConfigDAO;
import com.ares.app.content.vip.VipConfigDO;
import com.ares.framework.util.TimeUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


/**
 * ContentCacheManager loads caches for unit defs and store content and in addition
 * exposes stats and operations via jmx.
 * 
 * @author m.mcbride
 *
 */

@Component
public class ContentCacheManager
{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContentCacheManager.class);
	
	
	@Inject
	private VipConfigDAO vipConfigDAO;
	

	
	@Value("${store.cache.max.elements}")
	private int storeCacheMaxElements;
	
	@Value("${store.cache.max.time}")
	private int storeCacheMaxTime;
	
	@Value("${unitdef.cache.max.elements}")
	private int unitdefCacheMaxElements;
	
	@Value("${unitdef.cache.max.time}")
	private int unitdefCacheMaxTime;
	

	private LoadingCache<String,VipConfig>  vipConfig;

	
	public LoadingCache<String, VipConfig> getVipConfig() {
		return vipConfig;
	}
	
	
	private long lastSystemRefreshTime;

	private List<Cache> caches = new ArrayList<>();
	


	// post construct used to ensure config properties are set first before constructing cache.
	@PostConstruct
	private void setup(){
		this.systemRefreshed();
		
		System.out.println("=================  =========  "+ storeCacheMaxElements);

		
		vipConfig  = registerCache( CacheBuilder.newBuilder()
				.recordStats()
				.build(
						new CacheLoader<String, VipConfig>() {
							public VipConfig load( String version ) {
								return getVipConfigFromDAO( version );
							}
						} ), caches);

		}

	/**
	 * Registers a cache for automatic invalidation
	 *
	 * @param cache the cache to register
	 * @param <K>   cache key type
	 * @param <V>   cache value type
	 * @return the cache for assignment
	 */
	private <C extends Cache<K, V>, K, V> C registerCache( C cache, List<Cache> cacheList ) {
		cacheList.add( cache );
		return cache;
	}

	
	public void systemRefreshed() {
		lastSystemRefreshTime = TimeUtils.getCurrentUnixTime();
	}
	
	/**
	 * Last system refresh signifies the latter of the following two times:
	 * - Time when server was last restarted.
	 * - Time when game data was last published.
	 * Currently used for determining if battle replay is available.
	 * TODO - This approach should be redesigned as it's just an interim solution.
	 * 
	 * @return  last publish or server restart time, whichever is more recent
	 */
	public long getLastSystemRefreshTime() {
		return lastSystemRefreshTime;
	}
	
	@ManagedOperation(description="Invalidate all caches.")
	public void invalidateAll() {
		for( Cache cache : caches ) cache.invalidateAll();
		systemRefreshed();
	}
	
	private VipConfig    getVipConfigFromDAO(String key){
		VipConfigDO vipConfigDO = vipConfigDAO.findById(key);
		return vipConfigDO.getVipConfig();
	}
	
}
