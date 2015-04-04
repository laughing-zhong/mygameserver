package game.framework.util;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 
 * @author m.mcbride
 *
 * @param <T>
 */
@SuppressWarnings("SpellCheckingInspection")
public interface UnmarshalCommand<T>{

	T unmarshal (byte[] bytes) throws InvalidProtocolBufferException;
	
}
