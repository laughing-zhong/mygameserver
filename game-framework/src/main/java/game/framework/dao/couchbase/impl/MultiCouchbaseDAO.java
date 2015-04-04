package game.framework.dao.couchbase.impl;

import game.framework.dal.couchbase.CloseableCouchbaseClient;
import game.framework.dal.couchbase.CouchbaseDataSource;
import game.framework.dao.couchbase.IMultiCouchbaseDAO;
import game.framework.dao.couchbase.transcoder.ListOfStringsTranscoder;
import game.framework.dao.exception.DomainInstantiationException;
import game.framework.dao.exception.DAOException;
import game.framework.dao.exception.KeyAlreadyExistsException;
import game.framework.dao.exception.KeyNotFoundException;
import game.framework.domain.json.JsonDO;
import game.framework.domain.json.MultiJsonDO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * This dao works on MultiDomainObjects.
 *
 * @param <MultiDomainObject> MultiDomainObject that contains ids of <S>
 * @param <DomainObject>      SingleCouchbaseDomainObject
 * @author Brian
 */
public abstract class MultiCouchbaseDAO<MultiDomainObject extends MultiJsonDO<DomainObject>, DomainObject extends JsonDO> extends
		CouchbaseDAO<MultiDomainObject> implements IMultiCouchbaseDAO<MultiDomainObject, DomainObject> {
	private static final int MAX_TRIES_TO_APPEND = 3;


	/**
	 * class of MultiDomainObject
	 */
	protected final Class<MultiDomainObject> multiDomainClass;

	public MultiCouchbaseDAO( CouchbaseDataSource dataSource, Class<MultiDomainObject> multiDomainClass ) {
		super( dataSource, multiDomainClass );
		this.multiDomainClass = multiDomainClass;
	}

	/**
	 * Creates the list of ids to the database. If this list is already present
	 * in the database, this will fail.
	 * <p/>
	 * Note: This will NOT create or save the individual SingleDomainObjects that
	 * the ids in the list represent to the database. If you want changes to the
	 * SingleCouchbaseDomainObject that the ids represent to be saved to the database use
	 * their respective Dao.
	 */
	@Override
	public void create( MultiDomainObject objectToPersist ) throws DAOException {

		if ( objectToPersist.getId() == null ) throw new IllegalArgumentException( "invalid targetId" );

		String objectKey = getKeyFromId( objectToPersist.getId() );

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			boolean result = client.add( objectKey, 0, objectToPersist.getIdList(), ListOfStringsTranscoder.getInstance() ).get();

			if ( !result ) throw new KeyAlreadyExistsException( objectKey );

		} catch ( InterruptedException | ExecutionException e ) {
			throw new DAOException( e );
		}
	}

	/**
	 * Save the list of ids to the database. If this list isn't already present
	 * in the database, this will fail.
	 * <p/>
	 * Note: This will NOT create or save the individual SingleDomainObjects that
	 * the ids in the list represent to the database. If you want changes to the
	 * SingleCouchbaseDomainObject that the ids represent to be saved to the database use
	 * their respective Dao.
	 */
	@Override
	public void replace( MultiDomainObject objectToPersist ) throws KeyNotFoundException, DAOException {

		if ( objectToPersist.getId() == null ) throw new IllegalArgumentException( "invalid targetId" );

		String objectKey = getKeyFromId( objectToPersist.getId() );

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			boolean result = client.replace( objectKey, 0, objectToPersist.getIdList(), ListOfStringsTranscoder.getInstance() ).get();
			if ( !result ) throw new KeyNotFoundException( objectKey );

		} catch ( InterruptedException | ExecutionException e ) {
			throw new DAOException( e );
		}
	}

	/**
	 * Puts the list of ids to the database. This will write to the database
	 * whether or not the key already exists.
	 * <p/>
	 * Note: This will NOT create or save the individual SingleDomainObjects that
	 * the ids in the list represent to the database. If you want changes to the
	 * SingleCouchbaseDomainObject that the ids represent to be saved to the database use
	 * their respective Dao.
	 */
	@Override
	public void put( MultiDomainObject objectToPersist ) throws DAOException {

		if ( objectToPersist.getId() == null ) throw new IllegalArgumentException( "invalid targetId" );

		String objectKey = getKeyFromId( objectToPersist.getId() );

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			boolean result = client.set( objectKey, 0, objectToPersist.getIdList(), ListOfStringsTranscoder.getInstance() ).get();
			if ( !result ) throw new DAOException( "Could not set: " + objectKey );
		} catch ( InterruptedException | ExecutionException e ) {
			throw new DAOException( e );
		}

	}

	@Override
	public void append( MultiDomainObject list, DomainObject objectToPersist ) throws DAOException {
		append( list, objectToPersist.getId() );
	}

	/**
	 * Takes a MultiDomainObject and an targetId. Appends the targetId to the end of the
	 * object in the database. If that succeeds, it adds it to the
	 * MultiDomainObject.
	 * <p/>
	 * If the list does not already exist, it will create it instead with the
	 * newId appended to the end.
	 * <p/>
	 * Note: This will NOT create or save the individual SingleDomainObjects that
	 * the ids in the list represent to the database. If you want changes to the
	 * SingleCouchbaseDomainObject that the ids represent to be saved to the database use
	 * their respective Dao.
	 * <p/>
	 * Note 2: It is possible for other threads to be writing / append to the
	 * same key. So after this append, the multiDomainObject is still NOT
	 * guaranteed to represent what is in the database
	 *
	 * @param list  The MultiDomainObject the targetId is to be added to.
	 * @param newId The new targetId that should be appended to the end of the list
	 * @throws DAOException
	 */
	@Override
	public void append( MultiDomainObject list, String newId ) throws DAOException {
		if ( list.getId() == null ) {
			throw new IllegalArgumentException();
		}

		String key = getKeyFromId( list.getId() );

		// calling couchbase-client with a cas of 0 means that I don't care
		// what's in the database
		// append anyways. This isn't mentioned in their documentation though.
		boolean result = false;
		int current_tries = 0;


		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {

			while ( !result && current_tries < MAX_TRIES_TO_APPEND ) {
				try {
					result = client.append( 0, key, newId, ListOfStringsTranscoder.getInstance() ).get();
				} catch ( InterruptedException | ExecutionException e ) {
					throw new DAOException( e );
				}

				list.append( newId );

				// if the append failed because the key doesn't exist,
				// try to create it instead with the new targetId in the list
				if ( !result ) {
					try {
						result = client.add( key, 0, list.getIdList(), ListOfStringsTranscoder.getInstance() ).get();
					} catch ( InterruptedException | ExecutionException e ) {
						list.remove( newId );
						throw new DAOException( e );
					}

					// if it still failed (maybe another thread created it before
					// this one),
					// remove the new one from the list and try to append again
					if ( !result ) list.remove( newId );
				}

				current_tries++;
			}

			if ( !result ) {
				throw new DAOException( "Reached max tries, but cannot append!" );
			}

		}
	}

	/**
	 * If it can find the list of ids in the database, this will construct a new
	 * MultiDomainObject to return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public MultiDomainObject findById( String targetId ) throws DAOException {
		String key = getKeyFromId( targetId );

		// we need all the ids
		List<String> ids = null;

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			ids = (List<String>) client.get( key, ListOfStringsTranscoder.getInstance() );
		}

		if ( ids == null ) return null;

		return createMultiDomainObject( targetId, ids );
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<MultiDomainObject> findByIds( List<String> ids ) throws DAOException {
		List<String> keys = new ArrayList<>( ids.size() );
		for ( String id : ids ) {
			keys.add( getKeyFromId( id ) );
		}

		Map<String, Object> values = null;

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			values = client.getBulk( keys, ListOfStringsTranscoder.getInstance() );
		}


		List<MultiDomainObject> objs = new ArrayList<MultiDomainObject>( values != null ? values.size() : 0 );

		if ( values != null ) {

			int i = 0;
			for ( String key : keys ) {
				List<String> obj = (List<String>) values.get( key );
				if ( obj != null ) {
					MultiDomainObject multiDomainObject = createMultiDomainObject( ids.get( i ), obj );
					objs.add( multiDomainObject );
				}
				i++;
			}
		}

		return objs;
	}

	/**
	 * Creates a new multiDomainObject that has the targetId and the list of ids it
	 * contains
	 *
	 * @param id  The targetId of the MultiDomainObject
	 * @param ids The list of ids that this object will contain
	 * @return the new MultiDomainObject of type T
	 * @throws DAOException
	 */
	private MultiDomainObject createMultiDomainObject( String id, List<String> ids ) throws DAOException {
		MultiDomainObject multiDomainObject;
		try {
			multiDomainObject = multiDomainClass.newInstance();
			multiDomainObject.setId( id );
			multiDomainObject.setIdList( ids );
		} catch ( InstantiationException | IllegalAccessException e ) {
			throw new DomainInstantiationException( multiDomainClass );
		}

		return multiDomainObject;
	}

}
