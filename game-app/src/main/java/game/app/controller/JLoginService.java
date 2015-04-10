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
		userDo.setUserName("wesly");
		userDo.setId("1111111");
		userCbDAO.create(userDo);
		System.out.println("============  put cb  ");
		
		UserCbDO  cbUserDo = this.userCbDAO.findById(userDo.getId());
		System.out.println("--------------  "+cbUserDo.getUserName());
		return user;
	}

}
