package com.ares.framework.dao.redis.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.ares.framework.dao.redis.RedisCallback;
import com.ares.framework.dao.redis.RedisTemplate;

import redis.clients.jedis.Jedis;

@Component
public class RedisService {
	
	@Inject
	private RedisTemplate redisTemplate;
	
	private  static String   lua_casSet ="local nowValue = redis.call('get',KEYS[1])\n"
			+" if(nowValue == false) then\n"
			+" redis.call('set', KEYS[1], KEYS[2])  return 0 \n"
			+" end \n"
			+" local jsnNowValue = cjson.decode(nowValue)\n  "
			+" local nowCas = jsnNowValue['cas'] \n"
			+" if(nowCas ~= KEYS[3]) then\n"
			+" return 1\n"
			+" end \n"
			+" redis.call('set', KEYS[1], KEYS[2])  return 0 \n";
		
	
	private String lua_casSetSha ;
	
	public boolean setCASValue(long cas,String id,String value){
		String result = (String) excuteScript(lua_casSetSha,3,id,value,Long.toString(cas));
		System.out.println("++++++   result = "+result);
		if(result.equals("0")){
			return true;
		}
		return false;	
	}
	
	@PostConstruct
	public void Init(){
		lua_casSetSha  =  loadScriptSha(lua_casSet);
	}

	public String loadScriptSha(final String luaScript){
		return redisTemplate.execute(new RedisCallback<String>(){
			public String execute(Jedis jedis){
				return jedis.scriptLoad(luaScript);
			}
		});
	}
	
	public Object excuteScript(final String luaSha,final int keyCount, final String... params){
		return redisTemplate.execute(new RedisCallback<Object>(){
			public Object execute(Jedis jedis){
				return jedis.evalsha(luaSha, keyCount, params);
			}
		});
	}


	
}
