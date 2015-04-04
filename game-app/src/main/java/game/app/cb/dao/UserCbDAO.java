package game.app.cb.dao;

import javax.inject.Inject;

import game.app.domain.Do.UserDO;
import game.framework.dal.couchbase.CouchbaseDataSource;
import game.framework.dao.couchbase.impl.CasCouchbaseDAO;
import game.framework.dao.redis.EntityKey;
import game.framework.localcache.Cached;

import org.springframework.stereotype.Repository;



@Repository
@EntityKey("user")
@Cached(cacheCount = 100)
public class UserCbDAO extends CasCouchbaseDAO<UserDO>{
	
	@Inject
	public UserCbDAO(CouchbaseDataSource dataSource ){
		super(dataSource, UserDO.class);
	}

}
