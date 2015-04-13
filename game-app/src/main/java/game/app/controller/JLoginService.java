package game.app.controller;

import javax.inject.Inject;

import game.app.DO.User;
import game.app.cb.dao.UserCbDAO;
import game.app.domain.Do.UserCbDO;
import game.app.domain.Do.UserDO;
import game.framework.service.JIService;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
/**
 * 
* @ClassName: JLoginService
* @Description all service process the json proto should implement JIService
* @author wesly  wiqi.zhong@gmail.com
 */

@Component
public class JLoginService  implements JIService {
	@Inject
	private UserCbDAO  userCbDAO;
	public User  login(User user) throws JsonProcessingException
	{
		user.setUserName(user.getUserName()+" ok");
		
		UserCbDO  userDo = new UserCbDO();
		userDo.setUserName(user.getUserName());
		userDo.setId(user.getId());
		userCbDAO.create(userDo);
		userCbDAO.create(userDo);
		
		userCbDAO.put(userDo);
		System.out.println("============  put cb  ");
		
		for(int i = 0 ; i < 2; i++){
			UserCbDO  cbUserDo = this.userCbDAO.findById(userDo.getId());
			userCbDAO.put(cbUserDo);
			//userCbDAO.put(cbUserDo);
			System.out.println("--------------  "+cbUserDo.getUserName() + "cas = "+cbUserDo.getCas());
		}
		
		return user;
	}

}
