package game.app.service;

import game.app.domain.Do.UserDO;
import game.app.redis.dao.UserDAO;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Service;


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
