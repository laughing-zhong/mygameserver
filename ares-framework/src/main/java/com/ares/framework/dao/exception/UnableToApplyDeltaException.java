package com.ares.framework.dao.exception;

public class UnableToApplyDeltaException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnableToApplyDeltaException( String msg, Exception e ) {
		super( msg, e );
	}

	public UnableToApplyDeltaException( Exception e ) {
		super( e );
	}

	public UnableToApplyDeltaException( String msg ) {
		super( msg );
	}

	public UnableToApplyDeltaException() {}
}
