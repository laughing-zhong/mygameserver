package com.ares.framework.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Component;


@Component
public class ServiceMgr {

   @Inject
   private List<JIService> jRpcServices;
		
	@PostConstruct
	public void Init(){
		for(JIService  service : jRpcServices){
			String serviceName = service.getClass().getSimpleName();
			jServiceMaps.put(serviceName, service);
		}
	}
	
  
  public JIService  GetService(String serviceName){
	  return jServiceMaps.get(serviceName);
  }
	
    private   Map<String,JIService>  jServiceMaps = new HashMap<>();
}
