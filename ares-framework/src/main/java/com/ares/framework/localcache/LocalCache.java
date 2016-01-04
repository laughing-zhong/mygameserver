package com.ares.framework.localcache;

public interface LocalCache <T> {
	
	void cache(String key, T object);
	T    get(String key);
	void remove(String key);
	void clear();
}
