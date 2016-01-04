package com.ares.framework.rpc.json;

/**
 * 
* @ClassName: JsonResponse
* @Description: json response the hight layer for the json http response
* @author wesly  wiqi.zhong@gmail.com
 */
public class JsonResponse {
	
	
	private int  status; 
	private int reason;
	

	public int getReason() {
		return reason;
	}
	public void setReason(int reason) {
		this.reason = reason;
	}
	private byte[]   payLoad;
	public int getStatus() {
		return status;
	}
	public void setStatus(int state) {
		this.status = state;
	}
	public byte[] getPayLoad() {
		return payLoad;
	}
	public void setPayLoad(byte[] payLoad) {
		this.payLoad = payLoad;
	}
	public JsonResponse()
	{
		
	}
}
