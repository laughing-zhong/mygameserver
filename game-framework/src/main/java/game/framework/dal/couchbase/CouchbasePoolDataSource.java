package game.framework.dal.couchbase;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DataSource / Connection Pool for PoolableCouchbaseClients.
 * This class is not generally used, as the use case for it no longer exists.
 * It was introduced because spymemcached had a disconnect/reconnect problem that
 * under load caused relatively lengthy couchbase downtime. This was introduced
 * to ameliorate that problem, by maintaining a pool of already connected clients.
 * When one went bad, we could give out a good one from the pool without delay.
 * spymemcached seems to be fixed in the newer versions of CouchbaseClient.
 * <p/>
 * For performance reasons, we might want to use a ThreadLocal<PoolableCouchbaseClient> to give out
 * the same connection throughout a request.
 *
 * @author wesley
 */

public class CouchbasePoolDataSource extends GenericObjectPool<CloseableCouchbaseClient>
		implements CouchbaseDataSource {

	private static final Logger LOGGER = LoggerFactory.getLogger( CouchbasePoolDataSource.class );

	public CouchbasePoolDataSource() {}

	public CouchbasePoolDataSource( PoolableObjectFactory<CloseableCouchbaseClient> factory ) {
		super( factory );
	}

	@Override
	public CloseableCouchbaseClient borrowObject() {
		LOGGER.debug( "borrowObject()" );
		try {
			return super.borrowObject();
		} catch ( Exception e ) {
			LOGGER.error( "borrowObject() exception: {}", e );
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ea.vanguard.framework.dal.couchbase.CouchbaseDataSource#getConnection()
	 */
	@Override
	public CloseableCouchbaseClient getConnection() {
		CloseableCouchbaseClient client = borrowObject();
		if ( client != null ) client.setDataSource( this );

		return client;
	}

	@Override
	public void close( CloseableCouchbaseClient connection ) {
		returnObject( connection );
	}

	@Override
	public void shutdown() {
		LOGGER.info( "Shutting down pooled couchbase datasource." );
		try {
			close();
		} catch ( Exception e ) {
			LOGGER.info( "Issue shutting down pooled couchbase data source {}", e.getMessage() );
		}
	}

	@Override
	public void returnObject( CloseableCouchbaseClient connection ) {
		LOGGER.debug( "returnObject()" );
		if ( connection == null ) return;
		try {
			super.returnObject( connection );
		} catch ( Exception e ) {
			LOGGER.debug( "returnObject() exception: {} ", e );
		}
	}
}
