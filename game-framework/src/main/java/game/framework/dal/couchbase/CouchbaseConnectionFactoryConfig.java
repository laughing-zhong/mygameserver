package game.framework.dal.couchbase;

import java.util.List;

/**
 * Value object to assist in configuring couchbase via Spring.
 * @author wesly
 */


public class CouchbaseConnectionFactoryConfig {

	private String bucketName;
	private String password;
	private List<String> serverList;

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName( String bucketName ) {
		this.bucketName = bucketName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}

	public List<String> getServerList() {
		return serverList;
	}

	public void setServerList( List<String> serverList ) {
		this.serverList = serverList;
	}
}
