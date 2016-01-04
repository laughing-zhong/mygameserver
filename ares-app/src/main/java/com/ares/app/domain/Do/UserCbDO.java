package com.ares.app.domain.Do;

import java.util.ArrayList;
import java.util.List;






import org.springframework.stereotype.Repository;

import com.ares.framework.dao.redis.EntityKey;
import com.ares.framework.domain.json.CasJsonDO;
import com.ares.framework.localcache.Cached;

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
