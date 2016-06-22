package com.ares.app.DO;

import com.ares.framework.dao.mysql.Index;

import lombok.Data;


@Data
public class MyUser {
	@Index
	private int userid;
	private String name;
}
