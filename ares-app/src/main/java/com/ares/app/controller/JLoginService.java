package com.ares.app.controller;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.ares.app.DO.User;
import com.ares.app.cb.dao.UserCbDAO;
import com.ares.app.domain.Do.UserCbDO;
import com.ares.framework.dao.couchbase.IUpdateDO;
import com.ares.framework.dao.exception.DAOException;
import com.ares.framework.dao.exception.KeyNotFoundException;
import com.ares.framework.msg.publish.EventPublisher;
import com.ares.framework.service.JIService;
import com.fasterxml.jackson.core.JsonProcessingException;
/**
 * 
* @ClassName: JLoginService
* @Description all service process the json proto should implement JIService
* @author wesly  wiqi.zhong@gmail.com
 */

//         @Component
public class JLoginService  implements JIService {
	@Inject
	private UserCbDAO  userCbDAO;
	
	@Inject
	private EventPublisher   publisher;
	public User  login(User user) throws JsonProcessingException
	{
		user.setUserName(user.getUserName()+" ok");
		
		UserCbDO  userDo = new UserCbDO();
		userDo.setUserName(user.getUserName());
		userDo.setId(user.getId());
		userCbDAO.create(userDo);
		userCbDAO.create(userDo);
		
		userCbDAO.put(userDo);
		System.out.println("============  put cb   age = "+user.getAge());
		
		for(int i = 0 ; i < 2; i++){
			UserCbDO  cbUserDo = this.userCbDAO.findById(user.getId());
			userCbDAO.put(cbUserDo);
			userCbDAO.put(cbUserDo);
			System.out.println("--------------  "+cbUserDo.getUserName() + "cas = "+cbUserDo.getCas());
		}
		
	//	UserCbDO  cbUserDo = this.userCbDAO.findById(user.getId());
		IUpdateDO<Integer,UserCbDO> updateDo = new IUpdateDO<Integer,UserCbDO>(){
			@Override
			
			public UserCbDO applyDelta( Integer delta, UserCbDO objectToPersist ){
				objectToPersist.getItemIds().add(delta);
				return objectToPersist;
			}	
		};
		
	//	cbUserDo.getItemIds().add(user.getAge());
		try {
			userCbDAO.safeUpdate(updateDo, user.getAge(), user.getId());
		} catch (KeyNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		long begin = System.currentTimeMillis();
		for(int i = 0 ; i < 1000; ++i){
		  publisher.publisDaoError(""+i, "dfsdafsdfsdfsd");
		}
		long end = System.currentTimeMillis();
		System.out.println(" --------------  cost = "+(end -begin));
		return user;
	}
	

}
