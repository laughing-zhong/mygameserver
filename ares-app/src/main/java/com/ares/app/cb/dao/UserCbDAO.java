package com.ares.app.cb.dao;

import javax.inject.Inject;

import org.springframework.stereotype.Repository;

import com.ares.app.domain.Do.UserCbDO;
import com.ares.framework.dal.couchbase.CouchbaseDataSource;
import com.ares.framework.dao.couchbase.impl.CouchbaseDAO;
import com.ares.framework.dao.redis.EntityKey;


@Repository
@EntityKey("user")

public class UserCbDAO extends CouchbaseDAO<UserCbDO>{
	
	@Inject
	public UserCbDAO(CouchbaseDataSource dataSource ){
		super(dataSource, UserCbDO.class);
	}

}
