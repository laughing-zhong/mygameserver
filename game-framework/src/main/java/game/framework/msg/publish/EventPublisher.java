package game.framework.msg.publish;

import game.framework.dao.couchbase.transcoder.JsonObjectMapper;

import javax.annotation.PostConstruct;

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
	
	
	@PostConstruct
	public void Test(){
		publisDaoError("123","aaaaaaa");
	}
	
	public static class DaoErrorMsg{
		public DaoErrorMsg(String data){
			
		}
		private String playerId;

		public String getPlayerId() {
			return playerId;
		}

		public void setPlayerId(String playerId) {
			this.playerId = playerId;
		}
	}
}
