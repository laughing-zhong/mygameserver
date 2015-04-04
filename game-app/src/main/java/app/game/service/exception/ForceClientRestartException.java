package app.game.service.exception;

import game.framework.exception.CodedException;
import game.framwork.rpc.Rpc;

/**
 * @author wesley
 */
public class ForceClientRestartException extends RuntimeException implements CodedException {

	private static final long serialVersionUID = 1L;

	public ForceClientRestartException(final String message) {
		super(message);
	}

	public ForceClientRestartException(final Throwable cause) {
		super(cause);
	}

	public ForceClientRestartException(final String message, final Throwable cause) {
		super(message, cause);
	}

	@Override
	public Rpc.ResponseCode getCode() {
		return Rpc.ResponseCode.FORCE_CLIENT_RESTART;
	}
}
