package com.ares.framework.dao.mysql;

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
		set(myUser);
		System.out.println(" get ============= my_user name  = "+ myUser.getName());
	}

}
