package game.app.redis.dao;

import game.app.domain.Do.UserDO;
import game.framework.dao.redis.EntityKey;
import game.framework.dao.redis.RedisBaseDAO;

import org.springframework.stereotype.Repository;


@Repository
@EntityKey("user")
public class UserDAO extends RedisBaseDAO<UserDO>  {
	
	public UserDAO(){
		super(UserDO.class);
	}
	
}
