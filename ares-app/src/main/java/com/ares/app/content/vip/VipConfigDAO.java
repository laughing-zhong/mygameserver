package com.ares.app.content.vip;

import javax.inject.Inject;

import org.springframework.stereotype.Repository;

import com.ares.framework.dal.EntityKey;
import com.ares.framework.dal.couchbase.CouchbaseDataSource;
import com.ares.framework.dao.couchbase.impl.CouchbaseDAO;



@Repository
@EntityKey("vipconfig")
public class VipConfigDAO extends CouchbaseDAO<VipConfigDO> {

	@Inject
	public VipConfigDAO(CouchbaseDataSource dataSource) {
		super(dataSource, VipConfigDO.class);
		// TODO Auto-generated constructor stub
	}
}
