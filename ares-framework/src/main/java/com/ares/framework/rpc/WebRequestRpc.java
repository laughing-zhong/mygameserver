package com.ares.framework.rpc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import com.ares.framework.service.JIService;
import com.ares.framework.service.ServiceMgr;
import com.ares.service.exception.RunLogicException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;



public abstract class WebRequestRpc {
	private final static String ERROR_MSG_TAG = "error_msg";
	private final static String  ERROR_404 = "404";
	@Inject
	private ServiceMgr  serviceMgr;
	
	
	abstract public  void checkSession(HttpServletRequest req);
	abstract public  void postProcess();//no use
	
	@RequestMapping(value = "/view/{serviceName}/{methodName}",method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView   CallView(@PathVariable String serviceName,
			@PathVariable String  methodName,Model model,HttpServletRequest req ,HttpServletResponse response) throws JsonParseException, JsonMappingException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, IOException, InstantiationException
	{
	
		JIService service = serviceMgr.GetService(serviceName);
		if(service == null){
			model.addAttribute(ERROR_MSG_TAG, "can not find the service name :"+serviceName);
		    return new ModelAndView(ERROR_404);
		}
		
		Method method = this.GetMethod(service, methodName);
		if(method == null){
			model.addAttribute(ERROR_MSG_TAG, "can not find the method:"+methodName+"in the service: "+serviceName);
			 return new ModelAndView(ERROR_404);
		}
		//call method
		try {
			checkSession(req);
			ViewResponse result = CallObjMethod(service, method, req.getParameterMap(), model);
			if(result.Method != null || result.Service != null){
				RedirectView redirectView = new RedirectView();
				redirectView.setUrl(result.toString());
				return new ModelAndView(redirectView,result.getParams());
			}		
			return new ModelAndView(result.WebPage);
		}catch (InvocationTargetException e ){
			e.printStackTrace();
			RunLogicException  logicException = (RunLogicException) e.getTargetException();
			model.addAttribute(ERROR_MSG_TAG, logicException.getMsg());
			return new ModelAndView(logicException.getWebPage());
		}
	}
	
	
	@RequestMapping(value = "/rpc/{serviceName}/{methodName}",method = RequestMethod.POST )
	@ResponseBody
	public Object  CallRpc(@PathVariable String serviceName,
			@PathVariable String  methodName, HttpServletRequest req ) throws JsonParseException, JsonMappingException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, InstantiationException
	{
		JIService service = serviceMgr.GetService(serviceName);
		if(service == null){
		    return ERROR_404;
		}
		
		Method method = this.GetMethod(service, methodName);
		if(method == null){
			 return ERROR_404;
		}	
		checkSession(req);
		return  CallObjMethod(service, method, req.getParameterMap());
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
	
	
	private ViewResponse CallObjMethod(JIService service, Method method,
			Map<String, String[]> params, Model model)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, JsonParseException,
			JsonMappingException, IOException, InstantiationException {

		Class<?> methosParamType = method.getParameterTypes()[0];
		Object obj = null;
		if (!methosParamType.equals(Model.class))
			obj = methosParamType.newInstance();
		if (obj != null) {
			try {
				BeanUtils.populate(obj, params);
			} catch (IllegalAccessException  e) {
				e.printStackTrace();
			}
			return (ViewResponse) method.invoke(service, obj, model);
		}
		return (ViewResponse) method.invoke(service, model);
	}
	
	private  Object CallObjMethod(JIService service, Method method, Map<String,String[]> params) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JsonParseException, JsonMappingException, IOException, InstantiationException
	{
		int paramCount =  method.getParameterCount();
		if(paramCount > 0){
			  Class<?> methosParamType = method.getParameterTypes()[0];    
			  Object obj = methosParamType.newInstance();
			     if(IsStringType(methosParamType)){
			    	 Iterator<String[]>  stringParam = params.values().iterator();
			    	 if(stringParam.hasNext()){
			    	    String paramValue =  stringParam.next()[0];
			    	    return method.invoke(service, paramValue);   		
			    	 }
		          }
		       try {
					BeanUtils.populate(obj, params);
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}  
		   return method.invoke(service, obj);   		
		}
		return method.invoke(service);	
	}
	
	private  boolean IsStringType(Class<?> clazz) {   
	     return clazz.equals(String.class);      
	 }
}
