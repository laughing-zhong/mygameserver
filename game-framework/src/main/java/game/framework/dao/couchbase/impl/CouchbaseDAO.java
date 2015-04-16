package game.framework.dao.couchbase.impl;

import game.framework.dal.couchbase.CloseableCouchbaseClient;
import game.framework.dal.couchbase.CouchbaseDataSource;
import game.framework.dao.couchbase.IUpdateDO;
import game.framework.dao.exception.DAOException;
import game.framework.dao.exception.KeyNotFoundException;
import game.framework.dao.exception.OutOfDateDomainObjectException;
import game.framework.dao.exception.UnableToApplyDeltaException;
import game.framework.domain.json.CasJsonDO;
import game.framework.domain.json.JsonDO;
import game.framework.localcache.Cached;
import game.framework.localcache.LocalCache;
import game.framework.localcache.LocalCacheImpl;
import game.framework.localcache.LocalCacheMock;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

public class CouchbaseDAO<DomainObject extends JsonDO> extends AbstractCouchbaseDAO<DomainObject> {

	private LocalCache<DomainObject>  localCached;
	Class<DomainObject>  domainObjectClass ;

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
		this.domainObjectClass = domainObjectClass;
		fillLocalCache(domainObjectClass);
	}

	
	private void  fillLocalCache(Class<DomainObject> domainObjectClass ){
		Cached  cached = this.getClass().getAnnotation(Cached.class);
		if(cached  == null){
			this.localCached = new LocalCacheMock<DomainObject>();
		}
		else{
			if( CasJsonDO.class.isAssignableFrom(domainObjectClass)){
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
	public CouchbaseDAO( CouchbaseDataSource dataSource) {
		super( dataSource );

	}

	/**
	 * Creates a T object on the couchbase database. If the object does not have
	 * a targetId, it will generate a UUID for the object
	 */
	public void create( DomainObject objectToPersist ) throws DAOException {

		if (Strings.isNullOrEmpty(objectToPersist.getId()))
			throw new IllegalArgumentException("invalid Id");

		String objectKey = getKeyFromId(objectToPersist.getId());

		CloseableCouchbaseClient client = dataSource.getConnection();

		localCached.cache(objectKey, objectToPersist);
		client.create(objectKey,objectToPersist);
	}

	/**
	 * Saves a T object on the couchbase database. If the object does not have an
	 * Id, this will fail.
	 * <p/>
	 * If the object does not exist on the couchbase database then it will fail.
	 */
	public void replace( DomainObject objectToPersist ) throws KeyNotFoundException, DAOException {

		if (Strings.isNullOrEmpty(objectToPersist.getId()))
			throw new IllegalArgumentException("invalid Id");

		String objectKey = getKeyFromId(objectToPersist.getId());
		CloseableCouchbaseClient client = dataSource.getConnection();
		client.replace(objectKey, objectToPersist);
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
		localCached.cache(objectKey, objectToPersist);
	    CloseableCouchbaseClient client = dataSource.getConnection();
		 client.set( objectKey, expiry, objectToPersist );
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
		CloseableCouchbaseClient client = dataSource.getConnection() ;
		return client.get( objectKey, domainObjectClass);	
	}

	@Override
	public DomainObject findById( CloseableCouchbaseClient client, String targetId ) {
		if ( Strings.isNullOrEmpty( targetId ) ) throw new IllegalArgumentException( "invalid Id" );
		String objectKey = getKeyFromId( targetId );
		 DomainObject obj = this.localCached.get(objectKey);
		 if(obj != null){
			 return obj;
		 }
		return client.get( objectKey, domainObjectClass);
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
//		for ( String targetId : targetIds ) {
//			if ( Strings.isNullOrEmpty( targetId ) ) continue;
//			targetKeys.add( getKeyFromId( targetId ) );
//		}
//
//		Map<String, DomainObject> resultMap;
//
//	    CloseableCouchbaseClient client = dataSource.getConnection();
//			resultMap = client.getBulk( targetKeys, transcoder );
//		}
//
//		List<DomainObject> resultList = new ArrayList<>( resultMap == null ? 0 : resultMap.size() );
//
//		/* Return the empty list early */
//		if ( resultMap == null ) return resultList;
//
//		for ( String objectKey : targetKeys ) {
//			DomainObject resultObject = resultMap.get( objectKey );
//			if ( resultObject != null ) resultList.add( resultObject );
//		}

	//	return resultList;
		return null;
	}


	@Override
	public boolean safeSave(DomainObject objectToPersist) {
		CloseableCouchbaseClient client = dataSource.getConnection() ;
		String objectKey = this.getKeyFromId(objectToPersist.getId());
		return client.safeSave(objectKey, objectToPersist);
	}


	@Override
	public <DeltaObject>  void safeUpdate(IUpdateDO<DeltaObject, DomainObject> callable,DeltaObject deltaObject, String targetId) {
	
		if ( Strings.isNullOrEmpty( targetId ) ) throw new IllegalArgumentException( "invalid Id" );
		String objectKey = getKeyFromId( targetId );
		CloseableCouchbaseClient client = dataSource.getConnection() ;
		client.safeUpdate(objectKey, deltaObject, domainObjectClass, callable);
	}


	@Override
	public void onFError(String targetId) {
		localCached.remove(targetId);
	}
}
