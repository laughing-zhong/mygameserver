package com.ares.framework.rpc;

import java.util.HashMap;
import java.util.Map;

public class RpcResponse {
	private  String method;
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}

	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public void append(String key, String value){
		this.params.put(key, value);
	}
	private  String service;
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	private  Map<String,String> params = new HashMap<String, String>();
	
	

}
