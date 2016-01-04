package com.ares.framework.dal.couchbase;

import java.util.ArrayList;
import java.util.List;

/**
 * Value object to assist in configuring couchbase via Spring.
 * @author wesly
 */


public class CouchbaseConnectionConfigBean {

	private String bucket;
	public String getBucket() {
		return bucket;
	}
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	private String password;
	private List<String> nodes = new ArrayList<String>();

	
	public List<String> getNodes() {
		return nodes;
	}
	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}
	public CouchbaseConnectionConfigBean(String strServers){
		String[] servers = strServers.split(",");
		for(int i = 0 ; i < servers.length; ++i){
			nodes.add(servers[i]);
		}	
	}


	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}


}
