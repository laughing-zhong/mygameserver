package com.ares.app.asyn.msg.listener;

import java.util.List;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ares.framework.dao.couchbase.IFAccessEorror;
import com.ares.framework.msg.publish.EventPublisher;


@Component("daoErrorListener")
public class DAOErrorMsgListener implements MessageListener{

	private static final Logger LOGGER = LoggerFactory.getLogger( DAOErrorMsgListener.class );
	
	@Inject
	private List<IFAccessEorror>  frameDAOErrorProcessList;
	
	@Override
	public void onMessage(Message message) {		
		if (message instanceof TextMessage) {
			try {
				String targetId = message.getStringProperty(EventPublisher.DAO_IO_ERROR);
				String data = ((TextMessage) message).getText();
				onError(targetId);	
				LOGGER.error("id ={} data{}",targetId,data);
				// send error msg to log server
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}	
	}
	
	private void onError(String targetId){
		for(IFAccessEorror  processer : frameDAOErrorProcessList){
			processer.onFError(targetId);
		}
	}
}
