package game.framework.rpc.json;


/**
 * 
* @ClassName: JsonRpcRequest
* @Description: the json rpc request message the high layer for the json request message
* @author wesly  wiqi.zhong@gmail.com
 */
public class JsonRpcRequest {
	

	private String serviceName;
	private String methodName;
	private String userId;
	private String platForm;
	
	public JsonRpcRequest()
	{
		
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPlatForm() {
		return platForm;
	}
	public void setPlatForm(String platForm) {
		this.platForm = platForm;
	}
	public byte[] getPayLoad() {
		return payLoad;
	}
	public void setPayLoad(byte[] payLoad) {
		this.payLoad = payLoad;
	}
	private byte[]  payLoad;

}
