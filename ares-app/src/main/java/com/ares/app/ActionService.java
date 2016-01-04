package com.ares.app;

import java.io.BufferedReader;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ares.app.DO.User;

import javax.servlet.http.*;

/**
 * 
* @ClassName: ActionService
* @Description: only for some test
* @author wesly  wiqi.zhong@gmail.com
 */
@Controller
@RequestMapping("/action")
public class ActionService {
	
	
	@RequestMapping("test")
	@ResponseBody
	public  String OnCall(HttpServletRequest request)
	{
		StringBuffer buf = new StringBuffer();
		String line = null;
		try
		{
			BufferedReader reader = request.getReader();
			while((line = reader.readLine()) != null)
			{
				buf.append(line);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("receive msg = "+buf.toString());
		return buf.toString() ;
	}
	
	
	@RequestMapping(value = "json_test",method = RequestMethod.POST)
	@ResponseBody
	public User OnjsonTest(@RequestBody User user)
	{
		System.out.println("UserName = "+user.getUserName());
		return user;
	}
	
	@PostConstruct
	public void Init()
	{
		System.out.println("------+++--uuuuuuuuu init");
	}

}
