package game.framework.dao.couchbase.transcoder;

import game.framework.config.Constants;
import net.spy.memcached.CachedData;
import net.spy.memcached.transcoders.Transcoder;

public class ByteArrayTranscoder implements Transcoder<byte[]> {

	@Override
	public boolean asyncDecode( CachedData cachedData ) {
		return false;
	}

	@Override
	public CachedData encode( byte[] byteArray ) {
		return new CachedData( Constants.COUCHBASE_CACHED_DATA_FLAGS_DEFAULT, byteArray, byteArray.length );
	}

	@Override
	public byte[] decode( CachedData cachedData ) {
		return cachedData.getData();
	}

	@Override
	public int getMaxSize() {
		return Constants.COUCHBASE_CACHED_DATA_SIZE_MAX;
	}
}