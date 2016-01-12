package com.ares.framework.rpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ares.framwork.rpc.Rpc;
import com.ares.framwork.rpc.Rpc.ResponseCode;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ares.framework.service.IService;
import com.ares.framework.service.ServiceMgr;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;


/**
 * 
* @ClassName: ServiceRPC
* @Description: the service controller core for the protobuf  rpc call
* @author wesly  wiqi.zhong@gmail.com
 */

@Controller
@RequestMapping("/prpc")
public class ProtocServiceRPC {
	
	@Inject
	private ServiceMgr  serviceMgr;
	

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Rpc.ResponseEnvelope  CallRpc(@RequestBody Rpc.RequestEnvelope reqeustEnvelope, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
	{
		Rpc.ResponseEnvelope.Builder responseEnvelope = Rpc.ResponseEnvelope.newBuilder();
		try {
			String serviceName = reqeustEnvelope.getServiceName();
			String methodName = reqeustEnvelope.getMethodName();

			IService service = serviceMgr.GetService(serviceName);
			if (service == null) {
				responseEnvelope.setCode(ResponseCode.NO_SERVICE);

			}
			Method method = this.GetMethod(service, methodName);
			if(method == null){
				responseEnvelope.setCode(ResponseCode.NO_METHOD);
			}
			Message result = this.CallObjMethod(service, method,reqeustEnvelope.getPlayLoad());
			responseEnvelope.setPayload(result.toByteString());
			
		} catch (Exception e) {
			responseEnvelope.setErrMessage(e.getMessage());
			responseEnvelope.setCode(ResponseCode.ERROR);
			e.printStackTrace();
		}
		return responseEnvelope.build();
	}
	
	private Method GetMethod(Object obj, String methodName)
	{
		Method[] methods = obj.getClass().getMethods();
		for(int i = 0 ; i < methods.length ; ++i){
			if(methods[i].getName().equals(methodName)){
				return methods[i];
			}
		}
		return null;
	}
	
	
	private  Message CallObjMethod(IService service, Method method,ByteString  requestData) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		 Class<?> methosParamType = method.getParameterTypes()[0]; 
		 Method parserMethod = methosParamType.getMethod("parseFrom", com.google.protobuf.ByteString.class);
		 Object paramObject = parserMethod.invoke(methosParamType, requestData);
		 return (Message) method.invoke(service, paramObject);
	}

}
