package com.ares.framework.exception;

import com.ares.framwork.rpc.Rpc;



/**
 * @author wesley
 */

public class ServiceException extends RuntimeException implements CodedException {
	protected static final long serialVersionUID = 1L;

	public ServiceException( final String message ) {
		super( message );
	}

	public ServiceException( final Throwable cause ) {
		super( cause );
	}

	public ServiceException( final String message, final Throwable cause ) {
		super( message, cause );
	}

	@Override
	public Rpc.ResponseCode getCode() {
		return Rpc.ResponseCode.SERVER_ERROR;
	}
}
