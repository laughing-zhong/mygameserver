package game.framework.dao.couchbase.transcoder;
import game.framework.config.Constants;
import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static game.framework.dao.couchbase.transcoder.JsonObjectMapper.objectMapper;

/**
 * @author brianj
 */
public class SingleObjectTranscoder<Element> implements Transcoder<Element> {
	private static final Logger LOGGER = LoggerFactory.getLogger( SingleObjectTranscoder.class );
	private static final String JSON_SEPARATOR = ",";

	private Class<Element> elementClass;

	public SingleObjectTranscoder( Class<Element> elementClass ) {
		this.elementClass = elementClass;
	}


	@Override
	public boolean asyncDecode( CachedData cachedData ) {
		return false;
	}

	@Override
	public CachedData encode( Element elementToEncode ) {
		String rawValue;
		try {
			rawValue = objectMapper.writeValueAsString( elementToEncode );
		} catch ( IOException e ) {
			throw new RuntimeException( e ); // because I can't throw regular exceptions here :(
		}

		LOGGER.debug( "raw encoding: " + rawValue );

		String finalValue = rawValue + JSON_SEPARATOR;

		LOGGER.debug( "final encoding: " + finalValue );

		byte[] byteArray = finalValue.getBytes();

		return new CachedData( Constants.COUCHBASE_CACHED_DATA_FLAGS_DEFAULT, byteArray, byteArray.length );
	}

	@Override
	public Element decode( CachedData cachedData ) {
		String rawValue = new String( cachedData.getData() );

		//logger.debug( "raw decoding: " + rawValue );

		String finalValue = rawValue.substring( 0, rawValue.length() - 1 );

		//logger.debug( "final decoding: " + finalValue );


		try {
			return objectMapper.readValue( finalValue, elementClass );
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}

	}

	@Override
	public int getMaxSize() {
		return Constants.COUCHBASE_CACHED_DATA_SIZE_MAX;
	}
}
