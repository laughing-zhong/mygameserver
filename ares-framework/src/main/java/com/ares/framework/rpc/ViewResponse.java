package com.ares.framework.rpc;

import java.util.HashMap;
import java.util.Map;

public class ViewResponse {
	public String WebPage;
	public String Method;
	public String Service;
	private Map<String, String> params = new HashMap<String, String>();
	public String toString(){
		StringBuilder sb = new StringBuilder();
		if(WebPage != null){
			sb.append(WebPage);
		}
		if(Service != null){
			sb.append("/");
			sb.append(Service);
		}
		if(Method != null){
			sb.append("/");
			sb.append(Method);
		}
		return "/view"+sb.toString();			
	}
	public void appendParam(String key, String value){
		params.put(key, value);
	}
	public boolean hasParam(){
		return (params.size() > 0);
	}
	public Map<String, String> getParams(){
		return params;
	}

}
