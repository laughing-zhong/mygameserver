package game.framework.dao.couchbase.impl;

import game.framework.config.Constants;
import game.framework.dal.couchbase.CloseableCouchbaseClient;
import game.framework.dal.couchbase.CouchbaseDataSource;
import game.framework.dao.couchbase.transcoder.ByteArrayTranscoder;
import game.framework.dao.couchbase.transcoder.ListOfStringsTranscoder;
import game.framework.dao.exception.DomainInstantiationException;
import game.framework.dao.exception.DAOException;
import game.framework.dao.exception.KeyAlreadyExistsException;
import game.framework.dao.exception.KeyNotFoundException;
import game.framework.domain.json.ByteArrayDO;
import com.google.common.base.Strings;
import net.spy.memcached.transcoders.Transcoder;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author brianj
 */
public class ByteArrayCouchbaseDAO<DomainObject extends ByteArrayDO> extends AbstractCouchbaseDAO<DomainObject> {


	protected final Transcoder<byte[]> transcoder;
	protected final Class<DomainObject> domainObjectClass;

	public ByteArrayCouchbaseDAO( CouchbaseDataSource dataSource, Class<DomainObject> domainObjectClass ) {
		super( dataSource );
		this.domainObjectClass = domainObjectClass;
		this.transcoder = new ByteArrayTranscoder();
	}

	@Override
	public void create( DomainObject objectToPersist ) throws DAOException {
		if ( Strings.isNullOrEmpty( objectToPersist.getId() ) ) throw new IllegalArgumentException( "invalid targetId" );

		String objectKey = getKeyFromId( objectToPersist.getId() );

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			LOGGER.debug( "creating: {}", objectKey );
			boolean result = client.add( objectKey, ZERO_EXPIRATION, objectToPersist.getByteArray(), transcoder ).get();

			if ( !result ) throw new KeyAlreadyExistsException( objectKey );

		} catch ( InterruptedException | ExecutionException e ) {
			throw new DAOException( e );
		}
	}

	@Override
	public void replace( DomainObject objectToPersist ) throws KeyNotFoundException, DAOException {
		if ( Strings.isNullOrEmpty( objectToPersist.getId() ) ) throw new IllegalArgumentException( "invalid targetId" );

		String objectKey = getKeyFromId( objectToPersist.getId() );

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			LOGGER.debug( "replacing key: {}", objectKey );

			boolean result = client.replace( objectKey, ZERO_EXPIRATION, objectToPersist.getByteArray(), transcoder ).get();

			if ( !result ) throw new KeyNotFoundException( objectKey );
		} catch ( InterruptedException | ExecutionException e ) {
			throw new DAOException( e );
		}
	}

	@Override
	public void put( DomainObject objectToPersist ) throws DAOException {

		if ( Strings.isNullOrEmpty( objectToPersist.getId() ) ) throw new IllegalArgumentException( "invalid targetId" );

		String objectKey = getKeyFromId( objectToPersist.getId() );

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			boolean result = client.set( objectKey, ZERO_EXPIRATION, objectToPersist.getByteArray(), transcoder ).get();

			if ( !result ) throw new DAOException( "Could not set: " + objectKey );
		} catch ( InterruptedException | ExecutionException e ) {
			throw new DAOException( e );
		}
	}

	public void append( byte[] newBytes, String... targetIds ) throws DAOException {
		appendOnlyToDatabase( joinIds( targetIds ), newBytes );
	}

	public void append( DomainObject list, DomainObject obj ) throws DAOException {
		append( list, obj.getByteArray() );
	}

	public void append( DomainObject targetByteArrayDomainObject, byte[] byteArrayToAppend ) throws DAOException {
		if ( Strings.isNullOrEmpty( targetByteArrayDomainObject.getId() ) ) {
			throw new IllegalArgumentException();
		}

		boolean result = appendOnlyToDatabase( targetByteArrayDomainObject.getId(), byteArrayToAppend );

		if ( !result ) {
			throw new DAOException( "Reached max tries, but cannot append!" );
		} else {
			targetByteArrayDomainObject.setByteArray( ArrayUtils.addAll( targetByteArrayDomainObject.getByteArray(), byteArrayToAppend ) );
		}
	}

	private boolean appendOnlyToDatabase( String targetId, byte[] byteArrayToAppend ) throws DAOException {
		String objectKey = getKeyFromId( targetId );

		// calling couchbase-client with a cas of 0 means that I don't care
		// what's in the database and force an
		// append anyways. This isn't mentioned in their documentation though.
		// TODO: comment all this in English

		boolean result = false;
		int current_tries = 0;

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			while ( !result && current_tries < Constants.COUCHBASE_APPEND_RETRIES_MAX ) {
				try {
					/**
					 * The get() below forces blocking by immediately querying the future.
					 * TODO: Asynch version of this for non-critical appends.
					 */

					result = client.append( DEFAULT_CAS_VALUE, objectKey, byteArrayToAppend, transcoder ).get();
				} catch ( InterruptedException | ExecutionException e ) {
					throw new DAOException( e );
				}

				// if the append failed because the key doesn't exist,
				// try to create it instead with the new targetId in the targetByteArrayDomainObject
				if ( !result ) {
					try {
						/**
						 * The get() below forces blocking by immediately querying the future.
						 * TODO: Asynch version of this for non-critical appends.
						 */

						result = client.add( objectKey, ZERO_EXPIRATION, byteArrayToAppend, transcoder ).get();
					} catch ( InterruptedException | ExecutionException e ) {
						throw new DAOException( e );
					}
				}

				++current_tries;
			}

		}
		return result;
	}


	@Override
	public DomainObject findById( CloseableCouchbaseClient client, String targetId ) throws DAOException {
		if ( Strings.isNullOrEmpty( targetId ) ) throw new IllegalArgumentException( "invalid targetId" );

		String objectKey = getKeyFromId( targetId );

		LOGGER.debug( "findingById: {}", objectKey );

		byte[] byteArray = client.get( objectKey, transcoder );

		if ( byteArray == null ) return null;

		return createByteArrayDomainObject( targetId, byteArray );
	}

	@Override
	public DomainObject findById( String targetId ) throws DAOException {
		if ( Strings.isNullOrEmpty( targetId ) ) throw new IllegalArgumentException( "invalid targetId" );

		String objectKey = getKeyFromId( targetId );

		LOGGER.debug( "findingById: {}", objectKey );

		byte[] byteArray = null;

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			byteArray = client.get( objectKey, transcoder );
		}

		if ( byteArray == null ) return null;

		return createByteArrayDomainObject( targetId, byteArray );
	}

	@Override
	public List<DomainObject> findByIds( List<String> targetIdList ) throws DAOException {
		List<String> targetKeys = new ArrayList<>( targetIdList.size() );
		for ( String targetId : targetIdList ) {
			targetKeys.add( getKeyFromId( targetId ) );
		}

		Map<String, Object> resultMap = null;

		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
			resultMap = client.getBulk( targetKeys, ListOfStringsTranscoder.getInstance() );
		}

		List<DomainObject> resultList = new ArrayList<>( resultMap == null ? 0 : resultMap.size() );

		if ( resultMap == null ) return resultList;

		int i = 0;
		for ( String targetKey : targetKeys ) {
			byte[] byteArray = (byte[]) resultMap.get( targetKey );
			if ( byteArray != null ) {
				resultList.add( createByteArrayDomainObject( targetIdList.get( i ), byteArray ) );
			}
			++i;
		}

		return resultList;
	}

	private DomainObject createByteArrayDomainObject( String targetId, byte[] byteArray ) throws DAOException {
		DomainObject byteArrayDomainObject;
		try {
			byteArrayDomainObject = domainObjectClass.newInstance();
			byteArrayDomainObject.setId( targetId );
			byteArrayDomainObject.setByteArray( byteArray );
		} catch ( InstantiationException | IllegalAccessException e ) {
			throw new DomainInstantiationException( domainObjectClass );
		}

		return byteArrayDomainObject;
	}
}
