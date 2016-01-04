package com.ares.service.exception;

public class FwNotSupportedException  extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FwNotSupportedException( final String message ) {
		super( message );
	}

	public FwNotSupportedException( final Throwable cause ) {
		super( cause );
	}

	public FwNotSupportedException( final String message, final Throwable cause ) {
		super( message, cause );
	}

}
