package com.ares.app.mysql.dao;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.ares.app.DO.MyUser;
import com.ares.framework.dao.mysql.DbUtilsTemplate;
import com.ares.framework.dao.mysql.MySqlBaseDAO;
import com.ares.framework.dao.mysql.Table;


@Component
@Table("myuser")
public class MyUserDAO extends MySqlBaseDAO<MyUser> {
	
	@Inject
	private DbUtilsTemplate  dbUtilsTemplate;
	
	public MyUserDAO(){
		super(MyUser.class);
	}
	
	
	
	@PostConstruct
	public void test(){
		MyUser myUser = get(132);
		myUser.setName("updated_name");
		List<MyUser> myUserList = getList(132);
		for(MyUser myUseri : myUserList){
			System.out.println("name = " + myUseri.getName());
		}
			
		for(int i = 0 ; i < 10; i ++){
			MyUser user = new MyUser();
			user.setName("test_"+ i);
			user.setUserid(i + 100);
			int ret = delete(i + 100);
			System.out.println("=======ret " +  ret);
		}
		System.out.println(" get ============= my_user name  = "+ myUser.getName());
		List<MyUser> myUserListUtils = dbUtilsTemplate.find( MyUser.class, "select * from myuser");
		
		for(int i = 0 ; i < myUserListUtils.size(); ++i){
			MyUser user  = myUserListUtils.get(i);
			System.out.println("++++++  userName = "+ user.getName());
		}
	}
}
