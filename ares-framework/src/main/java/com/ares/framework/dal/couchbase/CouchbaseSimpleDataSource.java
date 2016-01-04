package com.ares.framework.dal.couchbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ares.framework.msg.publish.EventPublisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;





/**
 * Implementation of CouchbaseDataSource that uses only one cb client for all the datasource clients.
 *
 * @author mark.mcbride
 */

public class CouchbaseSimpleDataSource implements CouchbaseDataSource {
	//private static final Logger LOGGER = LoggerFactory.getLogger( CouchbaseSimpleDataSource.class );

	//private static final int   COUCHBASE_CONNECTION = 8;
	private int couchBaseConnectionCount;
	


	//private CloseableCouchbaseClient client;
	private List<CloseableCouchbaseClient> clientList = new ArrayList<CloseableCouchbaseClient>();

	@Inject
	public CouchbaseSimpleDataSource(CouchbaseConnectionConfigBean config,int connectionCount,EventPublisher  eventPublisher) throws IOException {
		 CloseableCouchbaseClient client = new CloseableCouchbaseClientImpl(config,eventPublisher);
		clientList.add(client);

	}

	@Override
	public synchronized CloseableCouchbaseClient  getConnection() {
		currentIndex++;
		if(currentIndex >= couchBaseConnectionCount){
			currentIndex = 0;
		}
		return clientList.get(currentIndex);
	}

	public void shutdown() {
	
	}

	public void close( CloseableCouchbaseClient connection ) {
		// noop
	}
	private  volatile  int currentIndex ;


}
