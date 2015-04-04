package game.app.domain.Do;

import org.springframework.stereotype.Repository;


import game.framework.dao.redis.EntityKey;
import game.framework.domain.json.CasJsonDO;


@Repository
@EntityKey("user")
public class UserDO extends CasJsonDO {
	private String userName;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
