package com.ares.app.service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.ares.app.domain.Do.UserDO;
import com.ares.app.redis.dao.UserDAO;


@Service
public class RedisTestService {
	
	
	@Inject 
	private UserDAO userDAO;
	@PostConstruct
	public void test(){
		
		UserDO userDo = new UserDO();
		userDo.setId("123");
		userDo.setUserName("wesly");
		
		userDAO.put(userDo);
		
		UserDO  gotUserDO = userDAO.findById("123");
		System.out.println("get userName = "+gotUserDO.getUserName());
			
	}

}
