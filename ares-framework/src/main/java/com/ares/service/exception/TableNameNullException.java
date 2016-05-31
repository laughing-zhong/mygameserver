/**
 * Project Name:ares-framework
 * File Name:TableNameNullException.java
 * Package Name:com.ares.service.exception
 * Date:2016年5月31日上午11:40:08
 * Copyright (c) 2016, chenzhou1025@126.com All Rights Reserved.
 *
*/

package com.ares.service.exception;
/**
 * ClassName:TableNameNullException <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年5月31日 上午11:40:08 <br/>
 * @author   zhongwq
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class TableNameNullException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TableNameNullException(String msg){
		super(msg);
	}
}

