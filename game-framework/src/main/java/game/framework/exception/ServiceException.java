package game.framework.exception;

import game.framwork.rpc.Rpc;



/**
 * @author dadler
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
