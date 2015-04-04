package game.framework.dal.couchbase;

/**
 * DataSource hides the details of whether the couchbase client is pooled
 * or a single client instance is used by the app.
 *
 * @author mark.mcbride
 */


public interface CouchbaseDataSource {
	CloseableCouchbaseClient getConnection();

	void shutdown();

	void close( CloseableCouchbaseClient connection );
}