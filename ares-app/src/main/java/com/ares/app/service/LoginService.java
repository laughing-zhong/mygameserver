package com.ares.app.service;

import org.game.app.Login;
import org.springframework.stereotype.Service;

import com.ares.framework.service.IService;




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
