package game.framework.dal.couchbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.framework.msg.publish.EventPublisher;

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
	private static final Logger LOGGER = LoggerFactory.getLogger( CouchbaseSimpleDataSource.class );

	//private static final int   COUCHBASE_CONNECTION = 8;
	private int couchBaseConnectionCount;
	
	@Inject
	private EventPublisher  eventPublisher;
	//private CloseableCouchbaseClient client;
	private List<CloseableCouchbaseClient> clientList = new ArrayList<CloseableCouchbaseClient>();

	public CouchbaseSimpleDataSource(CouchbaseConnectionConfigBean config,int connectionCount) throws IOException {
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
//		LOGGER.info( "Shutting down couchbase simple datasource." );
//		for(CloseableCouchbaseClient client : clientList)
//		 client.shutdown( 5, TimeUnit.SECONDS );
	}

	public void close( CloseableCouchbaseClient connection ) {
		// noop
	}
	private  volatile  int currentIndex ;

}
