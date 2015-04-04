package app.game.service.exception;

import game.framework.exception.CodedException;
import game.framwork.rpc.Rpc;

public class BillingException extends RuntimeException implements CodedException {

	private static final long serialVersionUID = 1L;
	
	public BillingException( final String message ) {
		super( message );
	}

	public BillingException( final Throwable cause ) {
		super( cause );
	}

	public BillingException( final String message, final Throwable cause ) {
		super( message, cause );
	}

	@Override
	public Rpc.ResponseCode getCode() {
		//return Rpc.ResponseCode.BILLING_EXCEPTION;
		return null;
	}

}
