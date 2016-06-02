/**
 * Project Name:ares-framework
 * File Name:PrimaryKeyNullException.java
 * Package Name:com.ares.service.exception
 * Date:2016年5月31日下午6:05:21
 * Copyright (c) 2016, chenzhou1025@126.com All Rights Reserved.
 *
*/

package com.ares.service.exception;
/**
 * ClassName:PrimaryKeyNullException <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年5月31日 下午6:05:21 <br/>
 * @author   zhongwq
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class PrimaryKeyNullException  extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public PrimaryKeyNullException(String msg){
		super(msg);
	}

}

