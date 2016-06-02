package com.ares.framework.msg.publish;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.ares.framework.dao.couchbase.transcoder.JsonObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class EventPublisher {
	
	public static final String DAO_IO_ERROR  = "dao.error.player.id";
	

	private ObjectMapper objectMapper = JsonObjectMapper.getInstance();

	@Inject
	private JmsTemplate template;
	
	private static final Logger LOGGER = LoggerFactory.getLogger( EventPublisher.class );
	
	public void publisDaoError(final String playerId,String data) {
		DaoErrorMsg playerEvent = new DaoErrorMsg(data);
		playerEvent.setPlayerId(playerId);
		LOGGER.error("publish  dao access error id {} data = {}",playerId,data);

		try {
			final String message = objectMapper.writeValueAsString(playerEvent);
			template.send("FRAMEWORK.DAO.ERROR", new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					TextMessage textMessage = session.createTextMessage(message);
					textMessage.setText(message);
					textMessage.setStringProperty(DAO_IO_ERROR, playerId);
					return textMessage;
				}
			});
		} catch (JsonProcessingException e) {}
	}
	
	
//	@PostConstruct
//	public void Test(){
//		long begin = System.currentTimeMillis();
//		for(int i = 0 ; i < 10000; ++i)
//		   publisDaoError("123","aaaaaaa");
//		
//		long end = System.currentTimeMillis();
//		System.out.println("===============================  publish cost = "+(end - begin));
//	}
	
	public static class DaoErrorMsg{
		public DaoErrorMsg(String data){
		  this.data = data;	
		}
		private String playerId;
		private String data;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public String getPlayerId() {
			return playerId;
		}

		public void setPlayerId(String playerId) {
			this.playerId = playerId;
		}
	}
}
