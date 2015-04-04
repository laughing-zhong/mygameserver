package game.framework.dao.couchbase.transcoder;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;

import game.framework.config.Constants;
import com.google.common.base.Joiner;

/**
 * Transcoder that takes a list and turns it into a comma delimited string or vise versa
 *
 * @author Brian
 */
public final class ListOfStringsTranscoder implements Transcoder<Object> {
	private static final String DELIMITER = ",";
	private static final ListOfStringsTranscoder instance = new ListOfStringsTranscoder();

	private static final Joiner joiner = Joiner.on( DELIMITER ).skipNulls();

	public static ListOfStringsTranscoder getInstance() {
		return instance;
	}

	@Override
	public boolean asyncDecode( CachedData d ) {
		return false;
	}

	/**
	 * Takes in a object.
	 * <p/>
	 * If the object is a Collection, it assumes it's a Collection<String> and turns it into
	 * a comma-delimited string.
	 * <p/>
	 * If it is anything else, turns it into a string and append "," at the end.
	 * <p/>
	 * TODO: Unit test the hell out of this. Looks super messy.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CachedData encode( Object objectToEncode ) {
		byte[] byteArray;
		
		if ( objectToEncode instanceof Collection<?> ) {
			byteArray = joiner.join( (Collection<String>) objectToEncode ).getBytes();
		} else if ( objectToEncode instanceof String ) {
			byteArray = (objectToEncode + DELIMITER).getBytes();
		} else {
			// assuming there's no commas in calling toString
			byteArray = (objectToEncode.toString() + DELIMITER).getBytes();
		}

		return new CachedData( Constants.COUCHBASE_CACHED_DATA_FLAGS_DEFAULT, byteArray, byteArray.length );
	}

	/**
	 * Decodes the comma delimited string in the CachedData and returns it as a List<String>
	 */
	@Override
	public List<String> decode( CachedData cachedData ) {
		return new ArrayList<String>( Arrays.asList( (new String( cachedData.getData() )).split( DELIMITER ) ) );
	}

	@Override
	public int getMaxSize() {
		return Constants.COUCHBASE_CACHED_DATA_SIZE_MAX;
	}

}
