package com.ares.framework.dao.redis;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.ares.framework.dal.couchbase.CloseableCouchbaseClient;
import com.ares.framework.dal.util.EntityUtils;
import com.ares.framework.dao.couchbase.IDAO;
import com.ares.framework.dao.couchbase.transcoder.JsonObjectMapper;
import com.ares.framework.dao.exception.DAOException;
import com.ares.framework.dao.exception.KeyNotFoundException;
import com.ares.framework.dao.redis.service.RedisService;
import com.ares.framework.domain.json.CasJsonDO;
import com.ares.framework.util.IdUtils;
import com.ares.service.exception.FwNotSupportedException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import redis.clients.jedis.Jedis;

public class RedisBaseDAO<DomainObject  extends CasJsonDO> implements IDAO<DomainObject>{
	@Inject
	private RedisTemplate redisTemplate;
	
	@Inject
	private RedisService redisService;
	
	private String idTemplate = null;
	
	private Class<DomainObject> ownDomainClass;
	public  RedisBaseDAO(Class<DomainObject> classType){
		idTemplate      = EntityUtils.buildEntityKeyTemplateForClass(this.getClass());
		ownDomainClass  = classType;
	}
	
	private String generatorJsonStr(DomainObject  domaiObj){
		try {
			return JsonObjectMapper.getInstance().writeValueAsString(domaiObj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private DomainObject parseFromJsonStr(String jsonStr ){
		try {
			return JsonObjectMapper.getInstance().readValue(jsonStr,ownDomainClass);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void put(DomainObject value) {
	    String strValue = generatorJsonStr(value);
	    if(strValue == null){
	    	return ;
	    }
		String key = this.getKeyFromId(value.getId());
	    saveRedis(key,strValue);
	}
	
	
	@Override
	public DomainObject findById( String id){	
		final String key = this.getKeyFromId(id);
		return redisTemplate.execute(new RedisCallback<DomainObject>(){
			public DomainObject execute(Jedis jedis) {
				String ret = jedis.get(key); 
				return parseFromJsonStr(ret);
			}
		});
	}
	
	
	
	private String getKeyFromId(String id){	
		return String.format(idTemplate, id);
	}
	private Integer saveRedis(final String key, final String value){
	  return	redisTemplate.execute(new RedisCallback<Integer>() {
			public Integer execute(Jedis jedis) {
				String ret =  jedis.set(key,value);
				if(ret == null){
					return  1;
				}
				return 0;
			}
		});
	}
	
	
	//cas
	public boolean safePut(DomainObject value) throws JsonProcessingException{	
		String strValue = JsonObjectMapper.getInstance().writeValueAsString(value);
		String key = this.getKeyFromId(value.getId());
		return redisService.setCASValue(value.getCas(),key,strValue);	
	}

	@Override
	public void create(DomainObject objectToPersist) throws DAOException {
		objectToPersist.setId(IdUtils.generate());	
		this.put(objectToPersist);
	}

	@Override
	public void replace(DomainObject objectToPersist)
			throws KeyNotFoundException, DAOException {
		  this.put(objectToPersist);	
	}

	@Override
	public DomainObject findById(String... targetIds) throws DAOException {
		throw new FwNotSupportedException("not supported");
		
	}

	@Override
	public DomainObject findById(CloseableCouchbaseClient client,
			String targetId) throws DAOException {
		return null;
	}

	@Override
	public DomainObject findById(CloseableCouchbaseClient client,
			String... targetIds) throws DAOException {
		throw new FwNotSupportedException("not supported");
	}

	@Override
	public List<DomainObject> findByIds(List<String> ids) throws DAOException {
		// TODO Auto-generated method stub	
		List<DomainObject> domainObjectList = new ArrayList<DomainObject>(ids.size());
		for(String id : ids){
			String key = this.getKeyFromId(id);
			DomainObject obj =  this.findById(key);
			if(obj != null){
				domainObjectList.add(obj);
			}	
		}
		return domainObjectList;
	}

	@Override
	public boolean delete(DomainObject targetObject) throws DAOException {
		// TODO Auto-generated method stub
		return this.delete(targetObject.getId());
	}

	@Override
	public boolean delete(CloseableCouchbaseClient client,
			DomainObject targetObject) throws DAOException {
		// TODO Auto-generated method stub
		  throw new FwNotSupportedException(" no surpport");
	}

	@Override
	public boolean delete(String targetId) throws DAOException {
		// TODO Auto-generated method stub
		final String key = this.getKeyFromId(targetId);
		return redisTemplate.execute(new RedisCallback<Boolean>(){
			public Boolean execute(Jedis jedis) {
				Long ret = jedis.del(key); 
				if(ret == 1){
					return true;
				}
				return false;
			
			}
		});
	}

	@Override
	public boolean delete(CloseableCouchbaseClient client, String targetId)
			throws DAOException {
	   throw new FwNotSupportedException(" no surpport");
	}


	public String generateAndSetNewId(DomainObject targetObject) {
		   throw new FwNotSupportedException(" no surpport");
	}

	@Override
	public void onFError(String targetId) {
		// TODO Auto-generated method stub
		
	}

}
