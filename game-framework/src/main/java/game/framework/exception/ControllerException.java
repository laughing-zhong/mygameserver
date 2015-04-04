package game.framework.exception;

import game.framwork.rpc.Rpc;



/**
 * @author dadler
 */

public class ControllerException extends com.google.protobuf.ServiceException implements CodedException {
	private static final long serialVersionUID = 1L;
	
	private Rpc.ResponseCode code = Rpc.ResponseCode.ERROR;

	public ControllerException( final String message ) {
		super( message );
	}

	public ControllerException( final Throwable cause ) {
		super( cause );
		setCode( cause );
	}

	public ControllerException( final String message, final Throwable cause ) {
		super( message, cause );
		setCode( cause );
	}

	@Override
	public Rpc.ResponseCode getCode() {
		return code;
	}

	private void setCode( Throwable cause ) {
		if ( (cause != null) && cause instanceof CodedException ) code = ((CodedException) cause).getCode();
	}


}

