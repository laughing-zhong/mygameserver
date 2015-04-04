package game.framework.dal.couchbase;

import com.couchbase.client.CouchbaseConnectionFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CloseableCouchbaseClient Factory. Creates new instances of CloseableCouchbaseClient. Validates, activates
 * passivates, and destroys CouchbaseClients for use with a {@link org.apache.commons.pool.PoolableObjectFactory}. This implementation
 * creates a new CouchbaseClient (with its own threads) when  makeObject() is called, and shuts down these threads
 * when destroyObject() is called.
 *
 * @author wesley
 */

public class CouchbaseClientObjectFactory implements PoolableObjectFactory<CloseableCouchbaseClient> {

	private static final Logger LOGGER = LoggerFactory.getLogger( CouchbaseClientObjectFactory.class );
	private CouchbaseConnectionFactory couchbaseConnectionFactory;

	@Override
	public CloseableCouchbaseClient makeObject() throws Exception {
		LOGGER.debug( "makeObject() invoke" );

		CloseableCouchbaseClient client;
		try {
			client = new CloseableCouchbaseClient( couchbaseConnectionFactory );
		} catch ( Exception e ) {
			LOGGER.error( "makeObject() exception: {}", e );
			throw e;
		}

		LOGGER.debug( "madeObject" );

		return client;
	}

	@Override
	public void destroyObject( CloseableCouchbaseClient connection ) throws Exception {
		try {
			connection.shutdown();
		} catch ( Exception e ) {
			LOGGER.debug( "shutdown() Exception: {}", e );
		}
	}

	@Override
	public boolean validateObject( CloseableCouchbaseClient connection ) {
		LOGGER.debug( "validateObject()" );

		/*
				Implement code to test if this object is still good.
				 */
		return true;
	}

	@Override
	public void activateObject( CloseableCouchbaseClient connection ) throws Exception {
		LOGGER.debug( "activateObject()" );
	}

	/**
	 * Hibernate this CBClient. Currently a no-op.
	 *
	 * @param connection couchbase client connection
	 * @throws Exception
	 */
	@Override
	public void passivateObject( CloseableCouchbaseClient connection ) throws Exception {
		LOGGER.debug( "passivateObject()" );
	}

	public CouchbaseConnectionFactory getCouchbaseConnectionFactory() {
		return couchbaseConnectionFactory;
	}

	public void setCouchbaseConnectionFactory( CouchbaseConnectionFactory couchbaseConnectionFactory ) {
		this.couchbaseConnectionFactory = couchbaseConnectionFactory;
	}
}
