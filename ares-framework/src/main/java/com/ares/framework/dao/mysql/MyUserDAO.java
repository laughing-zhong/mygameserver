package com.ares.framework.dao.mysql;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;


@Component
@Table("myuser")
public class MyUserDAO extends MysqlDAO<MyUser> {
	
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
	}

}
