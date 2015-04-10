package game.web.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;
import game.framework.dal.couchbase.CloseableCouchbaseClient;
import game.framework.dal.couchbase.CouchbaseDataSource;
import game.framework.dao.redis.RedisCallback;
import game.framework.dao.redis.RedisTemplate;

import com.google.common.base.Splitter;

@Controller
public class HealthController {

	private static final Logger LOGGER = LoggerFactory.getLogger( HealthController.class );
	
	@Resource(name="dataSource")
	private DataSource ds;
	
	@Inject
	private RedisTemplate redisTemplate;
	
	@Inject
	private CouchbaseDataSource couchbaseDataSource;
	
	
	@RequestMapping("/health")
	@ResponseBody
	public ResponseEntity<List<ServiceStatus>> checkHealth(@RequestParam(value="check", defaultValue="") String check) {
		List<ServiceStatus> statusList = new ArrayList<>();
		HttpStatus httpStatus = HttpStatus.OK;
		for (String system : Splitter.on(",").split(check)){
			ServiceStatus serviceStatus = null;
			switch (system) {
			case "redis":
				 serviceStatus = new ServiceCheckTemplate().execute(system, new ServiceCheck(){
					public void check() {
						redisTemplate.execute(new RedisCallback<String>() {
							public String execute(Jedis jedis) {
								return jedis.ping();
							}
						});
					}
					
				});
				break;
			case "couchbase":
				serviceStatus = new ServiceCheckTemplate().execute(system, new ServiceCheck(){
					public void check() {
//						try ( CloseableCouchbaseClient client = couchbaseDataSource.getConnection(); ) {
//							client.get("ping");
//						}
					}
					
				});
				break;
			case "mysql":
				serviceStatus = new ServiceCheckTemplate().execute(system, new ServiceCheck(){
					public void check() {
						try (Connection conn = ds.getConnection()) {
							boolean isValid = conn.isValid(5000);
							if (!isValid) throw new RuntimeException("MySQL returned false for isValid check.");
						} catch (SQLException ex) {
							throw new RuntimeException (ex);
						}
					}
					
				});
				break;
			default:
				break;
			}
			statusList.add(serviceStatus);
			if (serviceStatus != null && !serviceStatus.isOnline()){
				httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
			}
		}
		
		return new ResponseEntity<List<ServiceStatus>>(statusList, httpStatus);
	}
	
	public static class ServiceStatus {
		
		private String service;
		private boolean online;
		private String message;
		private long latency;
		
		public ServiceStatus(){}
		
		public ServiceStatus(String service, boolean online, String message, long latency) {
			this.service = service;
			this.online = online;
			this.message = message;
			this.latency = latency;
		}

		public String getService() {
			return service;
		}

		public boolean isOnline() {
			return online;
		}

		public String getMessage() {
			return message;
		}

		public long getLatency() {
			return latency;
		}
		
	}
	
	public interface ServiceCheck {
		
		void check();
		
	}
	
	public class ServiceCheckTemplate {
		
		public ServiceStatus execute (String serviceName, ServiceCheck callback) {
			long start = System.currentTimeMillis();
			boolean online = true;
			String message = null;
			try {
				callback.check();
			} catch (Exception e) {
				online = false;
				message = e.getMessage();
				LOGGER.warn("Exception thrown when checking service status.", e);
			}
			return new ServiceStatus(serviceName, online, message, System.currentTimeMillis() - start);
		}
	}
	
}
