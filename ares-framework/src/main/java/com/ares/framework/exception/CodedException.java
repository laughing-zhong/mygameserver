package com.ares.framework.exception;

import com.ares.framwork.rpc.Rpc;

/**
 * @author wesley
 */

public interface CodedException {
	Rpc.ResponseCode getCode();
}
