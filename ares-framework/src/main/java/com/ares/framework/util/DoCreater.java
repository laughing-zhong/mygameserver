package com.ares.framework.util;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

public  class DoCreater  {
	
	public  static JsonDocument create(String targetId,JsonObject obj){
		return JsonDocument.create(targetId,obj);
	}
	
	public static JsonDocument create(String targetId,JsonObject obj,int expireTime){
		return JsonDocument.create(targetId, expireTime, obj);
	}

}
