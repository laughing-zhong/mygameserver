package com.ares.framework.localcache;

import com.ares.framework.domain.json.JsonDO;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;


public class LocalCacheImpl<T extends JsonDO>  implements LocalCache<T>{

	private 	ConcurrentLinkedHashMap<String, T> map ;
	public LocalCacheImpl(int cacheCount){
	 map = new   ConcurrentLinkedHashMap.Builder<String,T>().maximumWeightedCapacity(cacheCount).build(); 
	}
	@Override
	public void cache(String key, T object) {
		map.put(key, object);
	}

	@Override
	public T get(String key) {
		return map.get(key);
	}
	@Override
	public void remove(String key) {
	    map.remove(key);
		
	}
	@Override
	public void clear() {
		map.clear();	
	}

}
