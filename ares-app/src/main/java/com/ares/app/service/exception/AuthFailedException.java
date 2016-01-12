package com.ares.app.service.exception;

import com.ares.framework.exception.CodedException;

import com.ares.framwork.rpc.Rpc;


/**
 * @author wesley
 */
public class AuthFailedException extends RuntimeException implements CodedException {

	private static final long serialVersionUID = 1L;

	public AuthFailedException( final String message ) {
		super( message );
	}

	public AuthFailedException( final Throwable cause ) {
		super( cause );
	}

	public AuthFailedException( final String message, final Throwable cause ) {
		super( message, cause );
	}

	@Override
	public Rpc.ResponseCode getCode() {
		return Rpc.ResponseCode.AUTH_FAILED;
	}
}
