/**
 * Project Name:ares-framework
 * File Name:InventoryDO.java
 * Package Name:com.ares.framework.dao.mysql
 * Date:2016年6月2日上午11:26:05
 * Copyright (c) 2016, chenzhou1025@126.com All Rights Reserved.
 *
*/

package com.ares.framework.dao.mysql;

import lombok.Data;

/**
 * ClassName:InventoryDO <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年6月2日 上午11:26:05 <br/>
 * @author   zhongwq
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
@Data
public class InventoryDO {
	private String name;
	@PKey
	private int id;
	private int count;
}

