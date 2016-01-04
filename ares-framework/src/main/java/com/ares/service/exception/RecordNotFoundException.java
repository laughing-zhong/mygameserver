package com.ares.service.exception;



/**
 * @author dadler
 */
public class RecordNotFoundException extends RuntimeException  {

	private static final long serialVersionUID = 1L;
	
	public RecordNotFoundException( final String message ) {
		super( message );
	}

	public RecordNotFoundException( final Throwable cause ) {
		super( cause );
	}

	public RecordNotFoundException( final String message, final Throwable cause ) {
		super( message, cause );
	}


}
