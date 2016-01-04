package com.ares.framework.dao.exception;

/**
 * Thrown when an instance of a domain object cannot be created.
 */

public class DomainInstantiationException extends DAOException {
	private static final long serialVersionUID = 1L;

	public DomainInstantiationException( Class<?> clazz ) {
		super( "Cannot create a: " + clazz.getName() );
	}

}
