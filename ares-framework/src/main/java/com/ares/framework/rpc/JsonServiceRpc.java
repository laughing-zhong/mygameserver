package com.ares.framework.rpc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ares.framework.rpc.json.JsonResponse;
import com.ares.framework.rpc.json.JsonRpcRequest;
import com.ares.framework.rpc.json.MsgState;
import com.ares.framework.service.JIService;
import com.ares.framework.service.ServiceMgr;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * 
* @ClassName: JsonServiceRpc
* @Description: the controller layer core for the json rpc call
* @author wesly  wiqi.zhong@gmail.com
 */



@RequestMapping("/jrpc")
public abstract class JsonServiceRpc {
	@Inject
	private ServiceMgr  serviceMgr;
	private ObjectMapper objectMapper;
	
	public JsonServiceRpc(){
		
	}
	
	@PostConstruct
	public void Init(){
		objectMapper = new ObjectMapper();
	}
	
	abstract public  void checkSession(JsonRpcRequest req);
	abstract public  void postProcess();//no use
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public JsonResponse  JCallRpc(@RequestBody  JsonRpcRequest   request) throws JsonParseException, JsonMappingException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException
	{
	
		JsonResponse   response = new JsonResponse();
		JIService service = serviceMgr.GetService(request.getServiceName());
		if(service == null)
		{
			response.setStatus(MsgState.NO_SERVICE);
			return response;
		}
		String   methodName  = request.getMethodName();
		
		Method method = this.GetMethod(service, methodName);
		if(method == null)
		{
			response.setStatus(MsgState.NO_METHOD);
			return response;
		}
		response.setPayLoad( CallObjMethod(service, method, request.getPayLoad()));
		return response;
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
	
	private  byte[] CallObjMethod(JIService service, Method method, byte[]  requestData) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JsonParseException, JsonMappingException, IOException
	{
		 Class<?> methosParamType = method.getParameterTypes()[0];  
		 Object object =  objectMapper.readValue(requestData, methosParamType);
	     Object retObj =  method.invoke(service, object);
	     return objectMapper.writeValueAsBytes(retObj);       
	}
}
