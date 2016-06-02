package com.ares.framework.util;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 
 * @author wesley
 *
 * @param <T>
 */

public interface UnmarshalCommand<T>{

	T unmarshal (byte[] bytes) throws InvalidProtocolBufferException;
	
}
