package com.ares.framework.rpc.json;

import lombok.Data;


/**
 * 
* @ClassName: JsonRpcRequest
* @Description: the json rpc request message the high layer for the json request message
* @author wesly  wiqi.zhong@gmail.com
 */
@Data
public class JsonRpcRequest {
	private String serviceName;
	private String methodName;
	private String userId;
	private String platForm;
	private byte[] payLoad;
	private String sk;
	public JsonRpcRequest(){
		
	}
}
