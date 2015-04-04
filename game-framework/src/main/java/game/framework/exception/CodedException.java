package game.framework.exception;

import game.framwork.rpc.Rpc;

/**
 * @author dadler
 */

public interface CodedException {
	Rpc.ResponseCode getCode();
}
