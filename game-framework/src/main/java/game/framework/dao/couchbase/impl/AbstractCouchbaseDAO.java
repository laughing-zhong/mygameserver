package game.framework.dao.couchbase.impl;


import game.framework.dal.couchbase.CloseableCouchbaseClient;
import game.framework.dal.couchbase.CouchbaseDataSource;
import game.framework.dal.util.EntityUtils;
import game.framework.dao.couchbase.ICasCouchbaseDAO;
import game.framework.dao.couchbase.IDAO;
import game.framework.dao.exception.DAOException;
import game.framework.domain.json.JsonDO;



import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract base implementation for Couchbase DAOs.
 *
 * @param <DomainObject> this domain object type this DAO will operate on
 */

abstract class AbstractCouchbaseDAO<DomainObject extends JsonDO> implements ICasCouchbaseDAO<DomainObject> {
	protected final Logger LOGGER;

	protected static final int DEFAULT_CAS_VALUE = 0;
	protected static final int ZERO_EXPIRATION = 0;
	protected static final String SEPARATOR = ":";
	protected final CouchbaseDataSource dataSource;
	protected final Joiner idJoiner;
	private final String entityKeyTemplate;




	public AbstractCouchbaseDAO( CouchbaseDataSource dataSource ) {
		LOGGER = LoggerFactory.getLogger( getClass() );
		this.dataSource = dataSource;
		this.entityKeyTemplate = EntityUtils.buildEntityKeyTemplateForClass( getClass(), SEPARATOR );
		this.idJoiner = Joiner.on( SEPARATOR ).skipNulls();
	}

	public DomainObject findById( String... targetIds ) throws DAOException {
		return findById( joinIds( targetIds ) );
	}

	@Override
	public DomainObject findById( CloseableCouchbaseClient client, String... targetIds ) throws DAOException {
		return findById( client, joinIds( targetIds ) );
	}

	public String joinIds( String... targetIds ) {
		return idJoiner.join( targetIds );
	}

	/**
	 * This will take in the DomainObject targetId and construct the proper couchbase
	 * key to look up in the database
	 *
	 * @param targetId The targetId of the DomainObject
	 * @return The fully qualified key to look up in couchbase.
	 */
	protected String getKeyFromId( String targetId ) {
		return String.format( entityKeyTemplate, targetId );
	}



	public boolean delete( DomainObject targetObject ) throws DAOException {
		return delete( targetObject.getId() );
	}

	/*
		TODO: This delete code seems awkward here. We should fix the inheritance hierarchy to not suck at some point.
	 */

	public boolean delete( String targetId ) throws DAOException {
		if ( Strings.isNullOrEmpty( targetId ) ) throw new IllegalArgumentException( "invalid targetId" );

		String objectKey = getKeyFromId( targetId );
		CloseableCouchbaseClient client = dataSource.getConnection();
		return client.delete( objectKey );	
	}

	@Override
	public boolean delete( CloseableCouchbaseClient client, String targetId ) throws DAOException {
		if ( Strings.isNullOrEmpty( targetId ) ) throw new IllegalArgumentException( "invalid targetId" );

		String objectKey = getKeyFromId( targetId );
		return client.delete( objectKey );
	}

	@Override
	public boolean delete( CloseableCouchbaseClient client, DomainObject targetObject ) throws DAOException {
		return delete( client, targetObject.getId() );
	}
}
