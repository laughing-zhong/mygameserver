package game.framework.dao.couchbase.impl;


import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.transcoders.Transcoder;

import game.framework.config.Constants;
import game.framework.dal.couchbase.CloseableCouchbaseClient;
import game.framework.dal.couchbase.CouchbaseDataSource;
import game.framework.dao.couchbase.ICasCouchbaseDAO;
import game.framework.dao.couchbase.IUpdateDO;
import game.framework.dao.exception.DAOException;
import game.framework.dao.exception.KeyNotFoundException;
import game.framework.dao.exception.OutOfDateDomainObjectException;
import game.framework.dao.exception.UnableToApplyDeltaException;
import game.framework.domain.json.CasJsonDO;
import com.google.common.base.Strings;

/**
 * DAO implementation with CAS support, such as safeSave.
 *
 * @param <DomainObject> domain object type this DAO operates on.
 */


public abstract class CasCouchbaseDAO<DomainObject extends CasJsonDO> extends CouchbaseDAO<DomainObject> implements ICasCouchbaseDAO<DomainObject> {

	public CasCouchbaseDAO( CouchbaseDataSource dataSource, Class<DomainObject> domainObjectClass ) {
		super( dataSource, domainObjectClass );
	}

	public CasCouchbaseDAO( CouchbaseDataSource dataSource, Transcoder<DomainObject> transcoder ) {
		super( dataSource, transcoder );
	}

	@Override
	public void safeSave( DomainObject objectToPersist ) throws OutOfDateDomainObjectException, KeyNotFoundException, DAOException {

		if ( Strings.isNullOrEmpty( objectToPersist.getId() ) ) {
			throw new IllegalArgumentException( "invalid targetId" );
		}

		String key = getKeyFromId( objectToPersist.getId() );

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {

			CASResponse result = client.cas( key, objectToPersist.getCas(), objectToPersist, transcoder );

			if ( result == CASResponse.OK ) {
				objectToPersist.setCas( client.gets( key, transcoder ).getCas() );
			} else if ( result == CASResponse.EXISTS ) {
				throw new OutOfDateDomainObjectException( objectToPersist );
			} else {
				throw new KeyNotFoundException( key );
			}
		}
	}


	public void safeSave( CloseableCouchbaseClient client, DomainObject objectToPersist ) throws OutOfDateDomainObjectException, KeyNotFoundException, DAOException {

		if ( objectToPersist.getId() == null ) throw new IllegalArgumentException();

		String objectKey = getKeyFromId( objectToPersist.getId() );

		CASResponse result = client.cas( objectKey, objectToPersist.getCas(), objectToPersist, transcoder );

		if ( result == CASResponse.OK ) {
			objectToPersist.setCas( client.gets( objectKey, transcoder ).getCas() );
		} else if ( result == CASResponse.EXISTS ) {
			throw new OutOfDateDomainObjectException( objectToPersist );
		} else {
			throw new KeyNotFoundException( objectKey );
		}

	}


	@Override
	public <DeltaObject> DomainObject safeUpdate( IUpdateDO<DeltaObject, DomainObject> callable, DeltaObject deltaObject, String... targetIds ) throws KeyNotFoundException, DAOException, UnableToApplyDeltaException {

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {

			for ( int retry = 0; retry < Constants.COUCHBASE_UPDATE_RETRIES_MAX; ++retry ) {

				DomainObject obj = findById( targetIds[0] );
				if ( obj == null ) {
					throw new KeyNotFoundException( "Cannot find: " + idJoiner.join( targetIds ) );
				}

				DomainObject updatedObject = callable.applyDelta( deltaObject, obj );

				try {
					safeSave( client, updatedObject );
				} catch ( OutOfDateDomainObjectException e ) {
					try {
						// being clever in my random waiting function
						int random = getRandomWaitTime( retry );
						LOGGER.info( "Domain Object " + obj.getId() + " was out of date! Retries so far: " + retry + " Sleeping for: " + random );
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

	/**
	 * Not only finds the object but also adds the cas value to it. The cas value
	 * will be used later for calling safeSave with the object.
	 */
	@Override
	public DomainObject findById( String targetId ) {
		String objectKey = getKeyFromId( targetId );

		CASValue<DomainObject> result;

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			if ( client == null ) throw new RuntimeException( "Unable to retrieve a couchbase client connection." );

			result = client.gets( objectKey, transcoder );
			LOGGER.debug( "targetId: {} result: {}", targetId, result );
		}

		if ( result == null ) return null;

		if ( result.getValue() != null ) result.getValue().setCas( result.getCas() );

		return result.getValue();
	}


	public int getRandomWaitTime( int seed ) {
		return (int) (Constants.COUCHBASE_SLEEP_UPDATE - (Thread.currentThread().getId() + seed) % Constants.COUCHBASE_SLEEP_UPDATE);
	}

}
