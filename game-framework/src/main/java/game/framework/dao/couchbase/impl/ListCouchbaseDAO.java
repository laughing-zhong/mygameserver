package game.framework.dao.couchbase.impl;

import game.framework.config.Constants;
import game.framework.dal.couchbase.CloseableCouchbaseClient;
import game.framework.dal.couchbase.CouchbaseDataSource;
import game.framework.dao.couchbase.IListCouchbaseDAO;
import game.framework.dao.couchbase.IUpdateDO;
import game.framework.dao.couchbase.transcoder.ListOfObjectsTranscoder;
import game.framework.dao.couchbase.transcoder.SingleObjectTranscoder;
import game.framework.dao.exception.*;
import game.framework.domain.json.ListJsonDO;
import com.google.common.base.Strings;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.transcoders.Transcoder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Stores an appendable list into couchbase
 *
 * @author brianj
 */
public class ListCouchbaseDAO<T extends ListJsonDO<Element>, Element> extends AbstractCouchbaseDAO<T> implements IListCouchbaseDAO<T, Element> {

	protected final ListOfObjectsTranscoder<Element> transcoder;
	protected final SingleObjectTranscoder<Element> appendTranscoder;

	private Class<T> clazz;

	public ListCouchbaseDAO( CouchbaseDataSource dataSource, Class<T> clazz, Class<Element> subElement ) {
		super( dataSource );
		this.transcoder = new ListOfObjectsTranscoder<>( subElement );
		this.appendTranscoder = new SingleObjectTranscoder<>( subElement );
		this.clazz = clazz;
	}

	@Override
	public void create( T objectToPersist ) throws DAOException {
		if ( objectToPersist.getId() == null ) {
			throw new IllegalArgumentException();
		}
		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			boolean result = client.add( getKeyFromId( objectToPersist.getId() ), 0, objectToPersist.getElements(), transcoder ).get();

			if ( !result ) {
				throw new KeyAlreadyExistsException( getKeyFromId( objectToPersist.getId() ) );
			}
		} catch ( InterruptedException | ExecutionException e ) {
			throw new DAOException( e );
		}
	}

	@Override
	public void replace( T objectToPersist ) throws KeyNotFoundException, DAOException {
		if ( objectToPersist.getId() == null ) {
			throw new IllegalArgumentException();
		}

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			boolean result = client.replace( getKeyFromId( objectToPersist.getId() ), 0, objectToPersist.getElements(), transcoder ).get();
			if ( !result ) {
				throw new KeyNotFoundException( getKeyFromId( objectToPersist.getId() ) );
			}
		} catch ( InterruptedException | ExecutionException e ) {
			throw new DAOException( e );
		}
	}

	@Override
	public void put( T objectToPersist ) throws DAOException {
		if ( objectToPersist.getId() == null ) {
			throw new IllegalArgumentException();
		}

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			boolean result = client.set( getKeyFromId( objectToPersist.getId() ), 0, objectToPersist.getElements(), transcoder ).get();
			if ( !result ) {
				throw new DAOException( "Could not set: " + getKeyFromId( objectToPersist.getId() ) );
			}
		} catch ( InterruptedException | ExecutionException e ) {
			throw new DAOException( e );
		}
	}

	@Override
	public T findById( String targetId ) throws DAOException {

		if ( Strings.isNullOrEmpty( targetId ) ) {
			throw new IllegalArgumentException();
		}

		String key = getKeyFromId( targetId );

		CASValue<List<Element>> result = null;

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			result = client.gets( key, transcoder );
		}

		if ( result == null ) return null;

		return createListDomainObject( targetId, result );
	}

	/**
	 * Duplicate of above method using passed client.
	 * Must be called from a try-with-resources block to return client to pool.
	 * <p/>
	 * TODO: Refactor this
	 *
	 * @param targetId
	 * @param client
	 * @return
	 * @throws DAOException
	 */
	public T findById( CloseableCouchbaseClient client, String targetId ) throws DAOException {

		if ( Strings.isNullOrEmpty( targetId ) ) throw new IllegalArgumentException( "invalid Id" );

		String key = getKeyFromId( targetId );

		CASValue<List<Element>> result = null;

		result = client.gets( key, transcoder );

		if ( result == null ) return null;

		return createListDomainObject( targetId, result );
	}


	@Override
	public T findById( String... targetIds ) throws DAOException {
		return findById( joinIds( targetIds ) );
	}

	@Override
	public T findById( CloseableCouchbaseClient client, String... targetIds ) throws DAOException {
		return null;
	}

	@Override
	public List<T> findByIds( List<String> targetIds ) throws DAOException {
		List<T> resultList = new ArrayList<>( targetIds.size() );
		// sadly no bulk gets :( for couchbase

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			for ( String targetId : targetIds )
				resultList.add( findById( client, targetId ) );
		}
		return resultList;
	}

	@Override
	public void append( String targetId, List<Element> elementList ) throws DAOException {
		append( targetId, elementList, transcoder );
	}

	@Override
	public void append( String targetId, Element element ) throws DAOException {
		append( targetId, element, appendTranscoder );
	}

	@Override
	public void safeClear( T obj ) throws DAOException, KeyNotFoundException, OutOfDateDomainObjectException {
		if ( obj == null || obj.getId() == null ) {
			throw new IllegalArgumentException();
		}

		String key = getKeyFromId( obj.getId() );


		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {

			CASResponse result = client.cas( key, obj.getCas(), null, transcoder );

			if ( result != CASResponse.OK ) {
				if ( result == CASResponse.EXISTS ) {
					throw new OutOfDateDomainObjectException( obj );
				} else {
					throw new KeyNotFoundException( key );
				}
			}

			obj.setCas( client.gets( key, transcoder ).getCas() );

		}

		obj.getElements().clear();
	}

	@Override
	public void safeSave( T objectToPersist ) throws OutOfDateDomainObjectException, KeyNotFoundException, DAOException {

		if ( objectToPersist.getId() == null ) {
			throw new IllegalArgumentException();
		}

		String key = getKeyFromId( objectToPersist.getId() );


		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {

			CASResponse result = client.cas( key, objectToPersist.getCas(), objectToPersist.getElements(), transcoder );

			if ( result == CASResponse.OK ) {
				objectToPersist.setCas( client.gets( key, transcoder ).getCas() );
			} else if ( result == CASResponse.EXISTS ) {
				throw new OutOfDateDomainObjectException( objectToPersist );
			} else {
				throw new KeyNotFoundException( key );
			}
		}
	}

	/**
	 * Must be called from a try-with-resources block to return client to pool.
	 * <p/>
	 * TODO: Refactor this.
	 *
	 * @param obj
	 * @param client
	 * @throws OutOfDateDomainObjectException
	 * @throws KeyNotFoundException
	 * @throws DAOException
	 */
	public void safeSave( T obj, CloseableCouchbaseClient client ) throws OutOfDateDomainObjectException, KeyNotFoundException, DAOException {

		if ( obj.getId() == null ) {
			throw new IllegalArgumentException();
		}

		String key = getKeyFromId( obj.getId() );

		CASResponse result = client.cas( key, obj.getCas(), obj.getElements(), transcoder );

		if ( result == CASResponse.OK ) {
			obj.setCas( client.gets( key, transcoder ).getCas() );
		} else if ( result == CASResponse.EXISTS ) {
			throw new OutOfDateDomainObjectException( obj );
		} else {
			throw new KeyNotFoundException( key );
		}

	}


	@Override
	public <DeltaObject> T safeUpdate( IUpdateDO<DeltaObject, T> callable, DeltaObject deltaObject, String... targetIds ) throws KeyNotFoundException, DAOException, UnableToApplyDeltaException {

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {

			for ( int retry = 0; retry < Constants.COUCHBASE_UPDATE_RETRIES_MAX; ++retry ) {

				T originalObject = findById( client, targetIds );
				if ( originalObject == null ) throw new KeyNotFoundException( "Cannot find: " + idJoiner.join( targetIds ) );

				T updatedObject = callable.applyDelta( deltaObject, originalObject );

				try {
					safeSave( updatedObject, client );
				} catch ( OutOfDateDomainObjectException e ) {
					try {
						// being clever in my random waiting function
						int random = getRandomWaitTime( retry );
						LOGGER.debug( "Domain Object " + originalObject.getId() + " was out of date! Retries so far: " + retry + " Sleeping for: " + random );
						Thread.sleep( random );
					} catch ( InterruptedException e1 ) {
						// do nothing.  it's fine
					}
					continue;
				}

				return updatedObject;
			}

		}
		throw new DAOException( "Update for " + idJoiner.join( targetIds ) + " failed after " + Constants.COUCHBASE_UPDATE_RETRIES_MAX + " tries." );
	}

	private <E> void append( String id, E elementsToPersist, Transcoder<E> transcoder ) throws DAOException {
		if ( Strings.isNullOrEmpty( id ) ) throw new IllegalArgumentException( "invalid Id" );

		if ( elementsToPersist == null ) throw new IllegalArgumentException( "no elements to persist" );

		String objectKey = getKeyFromId( id );

		boolean result = false;

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			for ( int i = 0; i < Constants.COUCHBASE_UPDATE_RETRIES_MAX; ++i ) {
				try {
					// appending with the default cas value of 0 means that we don't care
					// what is in the database, append anyways
					result = client.append( DEFAULT_CAS_VALUE, objectKey, elementsToPersist, transcoder ).get();

					// if the result fails, it's because it didn't exist in the first place
					// if this result is false, it's because something else got around
					// to creating it first.  go back to trying to append
					if ( !result ) result = client.add( objectKey, 0, elementsToPersist, transcoder ).get();
				} catch ( InterruptedException | ExecutionException e ) {
					throw new DAOException( e );
				}

				if ( result ) return;
			}

		}

		throw new DAOException( "Append for " + id + " failed after " + Constants.COUCHBASE_UPDATE_RETRIES_MAX + " tries." );
	}

	private T createListDomainObject( String id, CASValue<List<Element>> result ) throws DAOException {
		T domainObject;
		try {
			domainObject = clazz.newInstance();
		} catch ( InstantiationException | IllegalAccessException e ) {
			throw new DomainInstantiationException( clazz );
		}
		domainObject.setId( id );
		domainObject.setCas( result.getCas() );
		domainObject.setElements( result.getValue() );

		return domainObject;
	}

	public int getRandomWaitTime( int seed ) {
		return (int) (Constants.COUCHBASE_SLEEP_UPDATE - (Thread.currentThread().getId() + seed) % Constants.COUCHBASE_SLEEP_UPDATE);
	}
}
