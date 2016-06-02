/**
 * Project Name:ares-framework
 * File Name:InventoryDAO.java
 * Package Name:com.ares.framework.dao.mysql
 * Date:2016年6月2日上午11:25:02
 * Copyright (c) 2016, chenzhou1025@126.com All Rights Reserved.
 *
*/

package com.ares.framework.dao.mysql;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * ClassName:InventoryDAO <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年6月2日 上午11:25:02 <br/>
 * @author   zhongwq
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */


@Component
@Table("inventory")
public class InventoryDAO  extends MySqlBaseDAO<InventoryDO>{
	public InventoryDAO(){
		super(InventoryDO.class);
	}
	
	@PostConstruct
	public void test(){
		InventoryDO inventDO = get(12);
		System.out.println("inventory name = " + inventDO.getName());
		
		LombokDO  testDo = new LombokDO();
	}

}

