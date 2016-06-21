package com.ares.framework.rpc.context;

import lombok.Data;

@Data
public class RpcContext {
	private String userID;
	private String accountID;
	private int port;
	private String sk;
	private String contentPath;

	private String serverName;

	public String getServerUrl() {
		return "http://" + serverName + ":" + port ;//+ contentPath;
	}
}
