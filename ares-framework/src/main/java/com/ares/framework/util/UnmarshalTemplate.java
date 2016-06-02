package com.ares.framework.util;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Template to reuse boilerplate jms message/proto unmarshalling code.
 * 
 * @author wesley
 *
 */

public class UnmarshalTemplate {
	
	private static final Logger LOGGER = LoggerFactory.getLogger( UnmarshalTemplate.class );

	public <T> T unmarshal (Message message, UnmarshalCommand<T> command) {
		T event = null;
		if (message instanceof BytesMessage) {
			BytesMessage byteMessage = (BytesMessage) message;
			try {
				byte[] bytes = new byte[(int)byteMessage.getBodyLength()];
				byteMessage.readBytes(bytes);
				return command.unmarshal(bytes);
			} catch (JMSException e) {
				throw new RuntimeException(e);
			} catch (InvalidProtocolBufferException e) {
				LOGGER.warn("Unable to parse proto message.", e);
			}
		} else {
			LOGGER.warn("Message sent in the wrong format, should be bytesmessage.", message);
		}
		return event;
	}
}
