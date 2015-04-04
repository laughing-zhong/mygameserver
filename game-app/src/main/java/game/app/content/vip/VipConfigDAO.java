package game.app.content.vip;

import javax.inject.Inject;

import org.springframework.stereotype.Repository;

import game.framework.dal.EntityKey;
import game.framework.dal.couchbase.CouchbaseDataSource;
import game.framework.dao.couchbase.impl.CouchbaseDAO;



@Repository
@EntityKey("vipconfig")
public class VipConfigDAO extends CouchbaseDAO<VipConfigDO> {

	@Inject
	public VipConfigDAO(CouchbaseDataSource dataSource) {
		super(dataSource, VipConfigDO.class);
		// TODO Auto-generated constructor stub
	}
}
