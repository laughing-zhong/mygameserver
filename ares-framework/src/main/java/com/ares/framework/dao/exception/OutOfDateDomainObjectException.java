package com.ares.framework.dao.exception;

import com.ares.framework.domain.KeyedDO;

/**
 * Exception thrown when an attempt is made to write a CasJsonDO to
 * couchbase, but the cas is out of date. In other words, another client
 * wrote to this record between the time it was queried from the database
 * and the time of the write.
 */


public class OutOfDateDomainObjectException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private KeyedDO<?> obj;

	public OutOfDateDomainObjectException( KeyedDO<?> obj ) {
		super( "Domain object out of date, object cas is older than db cas: " + obj.getId() );
		this.obj = obj;
	}

	public KeyedDO<?> getOutdatedObject() {
		return obj;
	}
}
