package game.framework.dao.couchbase.impl;

import java.util.concurrent.atomic.AtomicInteger;

public class CouchbaseRWCount {
	private static AtomicInteger  count = new AtomicInteger(0);
	
	public static void increament(){
		count.incrementAndGet();
	}
	
	public static int get(){
		return count.get();
	}
	public static void clear(){
		count.set(0);
	}

}
