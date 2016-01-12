package com.ares.app.service.exception;

import com.ares.framework.exception.CodedException;

import com.ares.framwork.rpc.Rpc;


/**
 * @author wesley
 */

public class UnauthorizedException extends RuntimeException implements CodedException {

	private static final long serialVersionUID = 1L;

	public UnauthorizedException( final String message ) {
		super( message );
	}

	public UnauthorizedException( final Throwable cause ) {
		super( cause );
	}

	public UnauthorizedException( final String message, final Throwable cause ) {
		super( message, cause );
	}

	@Override
	public Rpc.ResponseCode getCode() {
		return Rpc.ResponseCode.UNAUTHORIZED;
	}
}
