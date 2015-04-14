package game.app.domain.Do;

import java.util.ArrayList;
import java.util.List;

import game.framework.dao.redis.EntityKey;



import game.framework.domain.json.CasJsonDO;
import game.framework.domain.json.JsonDO;
import game.framework.localcache.Cached;

import org.springframework.stereotype.Repository;

@Repository
@EntityKey("user")

public class UserCbDO extends CasJsonDO {
	

	public UserCbDO(){}
	private String userName;

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
  private List<Integer> itemIds = new ArrayList<Integer>();

public List<Integer> getItemIds() {
	return itemIds;
}
public void setItemIds(List<Integer> itemIds) {
	this.itemIds = itemIds;
}
}
