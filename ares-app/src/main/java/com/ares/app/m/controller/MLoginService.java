/**
 *
 * Copyright 2016 
 * All right  reserved	
 * Created on 2016年6月22日 
 */
package com.ares.app.m.controller;

import org.springframework.stereotype.Component;

import com.ares.app.DO.User;
import com.ares.framework.service.JIService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *@author wesley E-mail:wiqi.zhong@gmail.com
 *
 */


@Component
public class MLoginService  implements JIService{
	
	public User  login(User user) throws JsonProcessingException
	{
		
		return user;
	}


}
