package com.ares.framework.dao.exception;

/**
 * A couchbase record with the same key already exists,
 * so the write failed.
 */
public class KeyAlreadyExistsException extends DAOException {
	private static final long serialVersionUID = 1L;

	public KeyAlreadyExistsException( String key ) {
		super( "Key already exists: " + key );
	}
}
