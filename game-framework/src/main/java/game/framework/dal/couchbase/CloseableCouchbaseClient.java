package game.framework.dal.couchbase;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * CouchbaseClient that implements the AutoCloseable interface. This subclass of {@link com.couchbase.client.CouchbaseClient}
 * allows use of try-with-resources and {@link com.ea.vanguard.framework.dal.couchbase.CouchbasePoolDataSource}.getConnection().
 * <p/>
 * This class is intended to be used along with an object pool to allow automatic retrieval/return of objects to the pool.
 *
 * @author dadler
 */

public class CloseableCouchbaseClient extends CouchbaseClient implements AutoCloseable {

	private static final Logger LOGGER = LoggerFactory.getLogger( CloseableCouchbaseClient.class );
	private CouchbaseDataSource dataSource;

	public CloseableCouchbaseClient( CouchbaseConnectionFactory connectionFactory ) throws IOException {
		super( connectionFactory );
	}

	@Override
	public void close() {
		try {
			if ( dataSource != null ) dataSource.close( this );
		} catch ( Exception e ) {
			LOGGER.error( "close(): exception returning CB connection: {}", e );
		} finally {
			dataSource = null;
		}
	}

	public void setDataSource( CouchbaseDataSource dataSource ) {
		this.dataSource = dataSource;
	}
}
