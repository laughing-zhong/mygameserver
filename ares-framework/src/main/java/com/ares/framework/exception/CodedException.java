package com.ares.framework.exception;

import game.framwork.rpc.Rpc;

/**
 * @author wesley
 */

public interface CodedException {
	Rpc.ResponseCode getCode();
}
