package com.ares.framework.dao.mysql;

import lombok.Data;


@Data
public class MyUser {
	@Index
	private int userid;
	private String name;
}
