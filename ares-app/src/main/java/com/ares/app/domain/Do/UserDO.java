package com.ares.app.domain.Do;

import lombok.Data;

import org.springframework.stereotype.Repository;

import com.ares.framework.dao.redis.EntityKey;
import com.ares.framework.domain.json.CasJsonDO;

@Repository
@EntityKey("user")
@Data
public class UserDO extends CasJsonDO {
	private String userName;
}
