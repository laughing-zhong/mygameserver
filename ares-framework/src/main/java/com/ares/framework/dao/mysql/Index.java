/**
 * Project Name:ares-framework
 * File Name:PKey.java
 * Package Name:com.ares.framework.dao.mysql
 * Date:2016年5月31日上午11:59:59
 * Copyright (c) 2016, chenzhou1025@126.com All Rights Reserved.
 *
*/

package com.ares.framework.dao.mysql;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * ClassName:PKey <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年5月31日 上午11:59:59 <br/>
 * @author   zhongwq
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
	String value() default "";
	String prefix() default "";
	String suffix() default "";
}

