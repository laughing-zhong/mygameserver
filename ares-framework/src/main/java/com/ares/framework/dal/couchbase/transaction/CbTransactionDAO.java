package com.ares.framework.dal.couchbase.transaction;

import org.springframework.stereotype.Repository;

import com.ares.framework.dal.couchbase.CouchbaseDataSource;
import com.ares.framework.dao.couchbase.impl.CouchbaseDAO;
import com.ares.framework.dao.redis.EntityKey;


@Repository
@EntityKey("trasaction")
public class CbTransactionDAO extends  CouchbaseDAO<CbTransaction>{

	public CbTransactionDAO(CouchbaseDataSource dataSource) {
		super(dataSource,CbTransaction.class);
		// TODO Auto-generated constructor stub
	}

}
