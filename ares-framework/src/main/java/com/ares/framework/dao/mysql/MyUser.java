package com.ares.framework.dao.mysql;

import lombok.Data;


@Data
public class MyUser {
	@PKey
	private int userid;
	private String name;

}
