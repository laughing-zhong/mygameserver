package game.app.asyn.msg.listener;

import game.framework.msg.publish.EventPublisher;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component("daoErrorListener")
public class DAOErrorMsgListener implements MessageListener{

	private static final Logger LOGGER = LoggerFactory.getLogger( DAOErrorMsgListener.class );
	@Override
	public void onMessage(Message message) {		
		if (message instanceof TextMessage) {
			try {
				String playerId = message.getStringProperty(EventPublisher.DAO_IO_ERROR);
				String data = ((TextMessage) message).getText();
				LOGGER.error("receive dao error id ={} data{}",playerId,data);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
}
