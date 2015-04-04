package game.framework.dao.couchbase.impl;

import game.framework.dal.couchbase.CloseableCouchbaseClient;
import game.framework.dal.couchbase.CouchbaseDataSource;
import game.framework.dao.couchbase.transcoder.JsonTranscoder;
import game.framework.dao.exception.DAOException;
import game.framework.dao.exception.KeyAlreadyExistsException;
import game.framework.dao.exception.KeyNotFoundException;
import game.framework.domain.json.JsonDO;
import game.framework.localcache.Cache;
import game.framework.localcache.Cached;
import game.framework.localcache.LocalCache;
import game.framework.localcache.LocalCacheImpl;
import game.framework.localcache.LocalCacheMock;

import com.google.common.base.Strings;

import net.spy.memcached.transcoders.Transcoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;






/**
 * Basic Dao that most of the game Daos should extend. Represents a single
 * objectKey/value store in couchbase. All daos that extends this should be scoped in
 * some way. As long as they are thread-safe, they can be scoped as Singletons.
 *
 * @param <DomainObject> domain object type that this DAO operates on
 */


public class CouchbaseDAO<DomainObject extends JsonDO> extends AbstractCouchbaseDAO<DomainObject> {
	protected final Transcoder<DomainObject> transcoder;
	private LocalCache<DomainObject>  localCached;

	//private LogServerService  logServerService;

	/**
	 * all CouchbaseDAOs that extends this class should call this
	 * constructor.
	 *
	 * @param dataSource        The IConnectionContainer to be based to it.
	 * @param domainObjectClass The SingleCouchbaseDomainObject class this Dao uses.
	 */
	public CouchbaseDAO( CouchbaseDataSource dataSource, Class<DomainObject> domainObjectClass ) {
		super( dataSource );
		this.transcoder = new JsonTranscoder<>( domainObjectClass );
	}

	
	private void  fillLocalCache(Class<DomainObject> domainObjectClass ){
		Cached  cached = domainObjectClass.getAnnotation(Cached.class);
		if(cached  == null){
			this.localCached = new LocalCacheMock<DomainObject>();
		}
		else{
			if(this.getClass().isAssignableFrom(CasCouchbaseDAO.class)){
				throw new RuntimeException(" CasCouchbaseDAO  not support  cached annotation");
			}
			this.localCached = new LocalCacheImpl<DomainObject>(cached.cacheCount());
		}
		
	}
	/**
	 * Useful for mocking with a custom transcoder. Shouldn't be used most of the
	 * time unless you need your own transcoder other than the standard json
	 * transcoder.
	 *
	 * @param dataSource The IConnectionContainer to be based to it.
	 * @param transcoder The transcoder you want to use.
	 */
	public CouchbaseDAO( CouchbaseDataSource dataSource, Transcoder<DomainObject> transcoder ) {
		super( dataSource );
		this.transcoder = transcoder;
	}

	/**
	 * Creates a T object on the couchbase database. If the object does not have
	 * a targetId, it will generate a UUID for the object
	 */
	public void create( DomainObject objectToPersist ) throws DAOException {

		if ( Strings.isNullOrEmpty( objectToPersist.getId() ) ) throw new IllegalArgumentException( "invalid Id" );

		String objectKey = getKeyFromId( objectToPersist.getId() );

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			LOGGER.debug( "creating: {}", objectKey );

			localCached.cache(objectKey, objectToPersist);
			boolean result = client.add( objectKey, 0, objectToPersist, transcoder ).get();
			if ( !result ) throw new KeyAlreadyExistsException( objectKey );

		} catch ( InterruptedException | ExecutionException e ) {
			throw new DAOException( e );
		}
	}

	/**
	 * Saves a T object on the couchbase database. If the object does not have an
	 * Id, this will fail.
	 * <p/>
	 * If the object does not exist on the couchbase database then it will fail.
	 */
	public void replace( DomainObject objectToPersist ) throws KeyNotFoundException, DAOException {

		if ( Strings.isNullOrEmpty( objectToPersist.getId() ) ) throw new IllegalArgumentException( "invalid Id" );

		String objectKey = getKeyFromId( objectToPersist.getId() );

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			LOGGER.debug( "replacing objectKey: {}", objectKey );

			boolean result = client.replace( objectKey, 0, objectToPersist, transcoder ).get();
			if ( !result ) throw new KeyNotFoundException( objectKey );
		} catch ( InterruptedException | ExecutionException e ) {
			throw new DAOException( e );
		}
	}

	/**
	 * Put document without expiring.
	 */
	public void put( DomainObject objectToPersist ) throws DAOException {
		put( objectToPersist, 0 );
	}

	/**
	 * Puts a T object on the couchbase database If the object does not have an
	 * Id, this will generate a UUID for it.
	 * <p/>
	 * This method will succeed regardless of whether or not it already exists in couchbase.
	 *
	 * @param expiry, time in seconds when object will expire.
	 */
	public void put( DomainObject objectToPersist, int expiry ) throws DAOException {

		if ( Strings.isNullOrEmpty( objectToPersist.getId() ) ) throw new IllegalArgumentException( "invalid Id" );

		String objectKey = getKeyFromId( objectToPersist.getId() );
		System.out.println("++++++++++++++++++++++++++++  key = "+objectKey);

		localCached.cache(objectKey, objectToPersist);
		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			boolean result = client.set( objectKey, expiry, objectToPersist, transcoder ).get();
			if ( !result ) throw new DAOException( "Could not set: " + objectKey );
		} catch ( InterruptedException | ExecutionException e ) {
			throw new DAOException( e );
		}

	}

	/**
	 * Looks up a T object by the targetId that is given. Returns the object if it
	 * finds it, otherwise will return null
	 *
	 * @param targetId The targetId of the object to look for
	 * @return The object found or null if none is found
	 */

	@Override
	public DomainObject findById( String targetId ) throws DAOException {
		if ( Strings.isNullOrEmpty( targetId ) ) throw new IllegalArgumentException( "invalid Id" );
		String objectKey = getKeyFromId( targetId );
		 DomainObject obj = this.localCached.get(objectKey);
		 if(obj != null){
			 return obj;
		 }
		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			return client.get( objectKey, transcoder );
		}

	}

	@Override
	public DomainObject findById( CloseableCouchbaseClient client, String targetId ) {
		if ( Strings.isNullOrEmpty( targetId ) ) throw new IllegalArgumentException( "invalid Id" );
		String objectKey = getKeyFromId( targetId );
		 DomainObject obj = this.localCached.get(objectKey);
		 if(obj != null){
			 return obj;
		 }
		return client.get( objectKey, transcoder );
	}

	/**
	 * Looks up the T objects by the list of targetIds that is given. If the object is
	 * found it will be returned the the list of objects. If it is not found, it
	 * will not be present in said list.
	 *
	 * @param targetIds The list of targetIds of objects that is requested to be looked up.
	 * @return The list of objects found
	 */
	public List<DomainObject> findByIds( List<String> targetIds ) throws DAOException {
		List<String> targetKeys = new ArrayList<>( targetIds.size() );
		for ( String targetId : targetIds ) {
			if ( Strings.isNullOrEmpty( targetId ) ) continue;
			targetKeys.add( getKeyFromId( targetId ) );
		}

		Map<String, DomainObject> resultMap;

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			resultMap = client.getBulk( targetKeys, transcoder );
		}

		List<DomainObject> resultList = new ArrayList<>( resultMap == null ? 0 : resultMap.size() );

		/* Return the empty list early */
		if ( resultMap == null ) return resultList;

		for ( String objectKey : targetKeys ) {
			DomainObject resultObject = resultMap.get( objectKey );
			if ( resultObject != null ) resultList.add( resultObject );
		}

		return resultList;
	}
}
