package game.framework.dao.couchbase.transcoder;

import game.framework.config.Constants;
import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static game.framework.dao.couchbase.transcoder.JsonObjectMapper.objectMapper;

/**
 * This transcoder should be able to handle storing a list of objects in a json format
 *
 * @author brianj
 */
public class ListOfObjectsTranscoder<Element> implements Transcoder<List<Element>> {
	private static final Logger LOGGER = LoggerFactory.getLogger( ListOfObjectsTranscoder.class );
	private static final String BEGIN_ARRAY = "[";
	private static final String END_ARRAY = "]";
	private static final String JSON_SEPARATOR = ",";
	private static final String EMPTY_LIST = "";

	private Class<Element> elementClass;

	public ListOfObjectsTranscoder( Class<Element> elementClass ) {
		this.elementClass = elementClass;
	}

	@Override
	public boolean asyncDecode( CachedData cachedData ) {
		return false;
	}

	@Override
	public CachedData encode( List<Element> elements ) {
		String finalValue = EMPTY_LIST;

		if ( elements != null ) {
			String rawValue;
			try {
				rawValue = objectMapper.writeValueAsString( elements );
			} catch ( IOException e ) {
				throw new RuntimeException( e ); // because I can't throw regular exceptions here :(
			}

			//logger.debug( "raw encoding: " + rawValue );
			finalValue = rawValue.substring( 1, rawValue.length() - 1 ) + JSON_SEPARATOR;
		}

		//logger.debug( "final encoding: " + finalValue );

		byte[] data = finalValue.getBytes();

		return new CachedData( Constants.COUCHBASE_CACHED_DATA_FLAGS_DEFAULT, data, data.length );
	}

	@Override
	public List<Element> decode( CachedData cachedData ) {
		String rawValue = new String( cachedData.getData() );

		//logger.debug( "raw decoding: " + rawValue );

		String subValue = (rawValue.equals( EMPTY_LIST )) ? EMPTY_LIST : rawValue.substring( 0, rawValue.length() - 1 );

		String finalValue = BEGIN_ARRAY + subValue + END_ARRAY;

		//logger.debug( "final decoding: " + finalValue );

		List<Element> elements;

		try {
			elements = objectMapper.readValue( finalValue, objectMapper.getTypeFactory().constructCollectionType( ArrayList.class, elementClass ) );
		} catch ( IOException e ) {
			throw new RuntimeException( e );
		}

		return elements;
	}

	@Override
	public int getMaxSize() {
		return Constants.COUCHBASE_CACHED_DATA_SIZE_MAX;
	}
}
