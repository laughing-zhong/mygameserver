package com.ares.framework.dao.redis;

/**
 * Exception used in the case we encountered an exception with redis.
 *
 * @author wesley
 */


public class RedisException extends RuntimeException  {
	protected static final long serialVersionUID = 1L;

	public RedisException( final String message ) {
		super( message );
	}

	public RedisException( final Throwable cause ) {
		super( cause );
	}

	public RedisException( final String message, final Throwable cause ) {
		super( message, cause );
	}


}
