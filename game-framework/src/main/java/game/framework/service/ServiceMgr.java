package game.framework.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Component;


@Component
public class ServiceMgr {

   @Inject
   private List<IService> rpcServices;
   
   @Inject
   private List<JIService> jRpcServices;
	
	
	@PostConstruct
	public void Init()
	{
		for(IService service : rpcServices){
			String serviceName = service.getClass().getSimpleName();
			serviceMaps.put(serviceName, service);
		}
		for(JIService  service : jRpcServices){
			String serviceName = service.getClass().getSimpleName();
			jServiceMaps.put(serviceName, service);
		}
	}
	
	
  public IService  GetService(String serviceName)
  {
	  return serviceMaps.get(serviceName);
  }
  
  public JIService  GetJService(String serviceName)
  {
	  return jServiceMaps.get(serviceName);
  }
	
  
    private   Map<String,JIService>  jServiceMaps = new HashMap<>();
	private   Map<String,IService>   serviceMaps = new HashMap<>();

}
