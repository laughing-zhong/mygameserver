package com.ares.app.domain.Do;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.springframework.stereotype.Repository;

import com.ares.framework.dao.redis.EntityKey;
import com.ares.framework.domain.json.CasJsonDO;

@Repository
@EntityKey("user")
@Data
public class UserCbDO extends CasJsonDO {
	

	public UserCbDO(){}
	private String userName;
	
    private List<Integer> itemIds = new ArrayList<Integer>();
}
