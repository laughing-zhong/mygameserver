package game.framework.dal.couchbase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;




/**
 * Implementation of CouchbaseDataSource that uses only one cb client for all the datasource clients.
 *
 * @author mark.mcbride
 */

public class CouchbaseSimpleDataSource implements CouchbaseDataSource {
	private static final Logger LOGGER = LoggerFactory.getLogger( CouchbaseSimpleDataSource.class );

	//private static final int   COUCHBASE_CONNECTION = 8;
	private int couchBaseConnectionCount;
	
	//private CloseableCouchbaseClient client;
	private List<CloseableCouchbaseClient> clientList = new ArrayList<CloseableCouchbaseClient>();

	public CouchbaseSimpleDataSource( CouchbaseConnectionFactoryBean factory,int connectionCount ) throws IOException {
		int maxRetries = 3;
		int timeToWait = 15000;
		couchBaseConnectionCount = connectionCount;
		Exception lastException = null;
	   
		
		for (int i = 0; i < couchBaseConnectionCount; ++i) {
			int tries = 0;
			while (tries < maxRetries) {
				tries++;
				CloseableCouchbaseClient client = null;
				try {
					CouchbaseConnectionFactoryBean  tmpFactory = new CouchbaseConnectionFactoryBean(factory.getConnectionFactoryConfig());
					client = new CloseableCouchbaseClient(tmpFactory);
					clientList.add(client);
					break;
				} catch (Exception e) {
					lastException = e;
					// http://www.couchbase.com/issues/browse/JCBC-324
					LOGGER.warn(
							"Unable to connect to couchbase, retrying in {} ms. {}",
							tries * timeToWait, e.getMessage());
					try {
						Thread.sleep(tries * timeToWait);
					} catch (InterruptedException ignore) {
					}
				}

				if (client == null) {
					LOGGER.warn(
							"Unable to connect to couchbase, app node is shutting down.",
							lastException);
					System.exit(-1);
				}
			}
		}
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
		LOGGER.info( "Shutting down couchbase simple datasource." );
		for(CloseableCouchbaseClient client : clientList)
		 client.shutdown( 5, TimeUnit.SECONDS );
	}

	public void close( CloseableCouchbaseClient connection ) {
		// noop
	}
	private  volatile  int currentIndex ;

}
