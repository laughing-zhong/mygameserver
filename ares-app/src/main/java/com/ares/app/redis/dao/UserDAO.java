package com.ares.app.redis.dao;

import org.springframework.stereotype.Repository;

import com.ares.app.domain.Do.UserDO;
import com.ares.framework.dao.redis.EntityKey;
import com.ares.framework.dao.redis.RedisBaseDAO;


@Repository
@EntityKey("user")
public class UserDAO extends RedisBaseDAO<UserDO>  {
	
	public UserDAO(){
		super(UserDO.class);
	}
	
}
