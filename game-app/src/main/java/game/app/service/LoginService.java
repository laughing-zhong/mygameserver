package game.app.service;

import game.framework.service.IService;

import org.game.app.Login;
import org.springframework.stereotype.Service;




@Service
public class LoginService  implements IService{
	
	public Login.ResponseLogin OnLogin(Login.RequestLogin requestLogin)
	{
		Login.ResponseLogin.Builder  loginResponse = Login.ResponseLogin.newBuilder();
		loginResponse.setCoin(12);
		loginResponse.setLvl(123);
		loginResponse.setNickName("wesly");
		loginResponse.setSessionId("sessionId");
		return loginResponse.build();
	}

}
