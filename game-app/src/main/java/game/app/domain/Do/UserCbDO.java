package game.app.domain.Do;

import game.framework.dao.redis.EntityKey;


import game.framework.domain.json.JsonDO;
import game.framework.localcache.Cached;

import org.springframework.stereotype.Repository;

@Repository
@EntityKey("user")
@Cached(cacheCount = 100)
public class UserCbDO extends JsonDO {
	

	public UserCbDO(){}
	private String userName;

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
