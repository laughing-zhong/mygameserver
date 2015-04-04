package game.framework.dao.couchbase.transcoder;

import game.framework.config.Constants;
import game.framework.domain.json.JsonDO;
import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

import static game.framework.dao.couchbase.transcoder.JsonObjectMapper.objectMapper;

/**
 * This transcoder is used for all our SingleDomainObjects to be stored in the
 * couchbase database. Takes a SingleCouchbaseDomainObject and serializes it to json.
 *
 * @param <DomainObject> CouchbaseDomainObject that this transcoder works on
 * @author Brian
 */
@Singleton
public class JsonTranscoder<DomainObject extends JsonDO> implements Transcoder<DomainObject> {
	private Class<DomainObject> domainObjectClass;
	private Logger LOGGER;


	@Inject
	public JsonTranscoder( Class<DomainObject> domainObjectClass ) {
		LOGGER = LoggerFactory.getLogger( getClass() );

		this.domainObjectClass = domainObjectClass;
	}

	@Override
	public boolean asyncDecode( CachedData cachedData ) {
		return false;
	}

	/**
	 * Takes in the cachedData, gets the byte array transforms to a json string,
	 * deserializes the string to object, cast it as a type T that extends a
	 * SingleCouchbaseDomainObject
	 */
	@Override
	public DomainObject decode( CachedData cachedData ) {
		DomainObject domainObject = null;

		try {
			domainObject = objectMapper.readValue( cachedData.getData(), domainObjectClass );
		} catch ( IOException e ) {
			LOGGER.error( "decode() IOException: ", e ); // fubar
		}
		LOGGER.debug( "decode() [{}] {}", domainObjectClass.getName(), new String( cachedData.getData() ) );

		return domainObject;
	}

	/**
	 * Takes in an object of type T that extends a SingleCouchbaseDomainObject, serializes
	 * it to a json string, gets the byte array, returns a cachedData with that
	 * byte array in it
	 */
	@Override
	public CachedData encode( DomainObject domainObject ) {
		byte[] byteArray = null;
		int dataLength = 0; // prevent null pointer exception
		try {
			byteArray = objectMapper.writeValueAsBytes( domainObject );
			LOGGER.debug( "encode() [{}] {}", domainObjectClass.getName(), new String( byteArray ) );
			dataLength = byteArray.length;
		} catch ( IOException e ) {
			LOGGER.error( "encode() IOException: ", e ); // fubar
		}

		return new CachedData( Constants.COUCHBASE_CACHED_DATA_FLAGS_DEFAULT, byteArray, dataLength );
	}

	@Override
	public int getMaxSize() {
		return Constants.COUCHBASE_CACHED_DATA_SIZE_MAX;
	}

}
