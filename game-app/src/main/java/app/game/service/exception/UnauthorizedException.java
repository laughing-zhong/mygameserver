package app.game.service.exception;

import game.framework.exception.CodedException;
import game.framwork.rpc.Rpc;


/**
 * @author dadler
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
