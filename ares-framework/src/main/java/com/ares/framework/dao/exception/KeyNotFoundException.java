package com.ares.framework.dao.exception;

/**
 * A couchbase key could not be found.
 */


public class KeyNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public KeyNotFoundException( String key ) {
		super( "Key not found: " + key );
	}
}
