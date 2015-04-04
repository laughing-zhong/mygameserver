package game.framework.dal.couchbase;

import com.couchbase.client.CouchbaseConnectionFactory;
import game.framework.util.UriUtils;

import java.io.IOException;
import java.util.List;

/**
 * CouchbaseConnectionFactory that can accept  a{@link com.ea.vanguard.framework.dal.couchbase.CouchbaseConnectionFactoryConfig}
 * as a constructor parameter. Also accepts List<String>, a list of server addresses. Convenience bean that
 * can easily be configured from Spring XML.
 *
 * @author wesley
 */

public class CouchbaseConnectionFactoryBean extends CouchbaseConnectionFactory {

	public CouchbaseConnectionFactoryBean( final List<String> serverStringList, final String bucketName, final String password ) throws IOException {
		super( UriUtils.stringListAsUriList( serverStringList ), bucketName, password );
	}

	public CouchbaseConnectionFactoryBean( CouchbaseConnectionFactoryConfig connectionFactoryConfig ) throws IOException {
		this( connectionFactoryConfig.getServerList(), connectionFactoryConfig.getBucketName(), connectionFactoryConfig.getPassword() );
		this.connectionFactoryConfig = connectionFactoryConfig;
	}
	private CouchbaseConnectionFactoryConfig connectionFactoryConfig;
	public CouchbaseConnectionFactoryConfig getConnectionFactoryConfig() {
		return connectionFactoryConfig;
	}

	public void setConnectionFactoryConfig(
			CouchbaseConnectionFactoryConfig connectionFactoryConfig) {
		this.connectionFactoryConfig = connectionFactoryConfig;
	}

}
