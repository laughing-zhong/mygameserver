package game.app.cb.dao;

import javax.inject.Inject;

import game.app.domain.Do.UserCbDO;
import game.framework.dal.couchbase.CouchbaseDataSource;

import game.framework.dao.couchbase.impl.CouchbaseDAO;
import game.framework.dao.redis.EntityKey;
import game.framework.localcache.Cached;

import org.springframework.stereotype.Repository;



@Repository
@EntityKey("user")

public class UserCbDAO extends CouchbaseDAO<UserCbDO>{
	
	@Inject
	public UserCbDAO(CouchbaseDataSource dataSource ){
		super(dataSource, UserCbDO.class);
	}

}
