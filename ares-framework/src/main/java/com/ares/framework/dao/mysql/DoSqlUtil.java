/**
 * Project Name:ares-framework
 * File Name:PrePareSql.java
 * Package Name:com.ares.framework.dao.mysql
 * Date:2016年5月31日下午5:21:49
 * Copyright (c) 2016, chenzhou1025@126.com All Rights Reserved.
 *
*/

package com.ares.framework.dao.mysql;

import java.lang.reflect.Field;

import com.ares.service.exception.PrimaryKeyNullException;

/**
 * ClassName:PrePareSql <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年5月31日 下午5:21:49 <br/>
 * @author   zhongwq
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class DoSqlUtil {	
	public static Object[] getInsertFields(Class<?>doClass){
		Field[] fields = doClass.getDeclaredFields();
		int fieldCount = fields.length;
		Object[] filedsArray = new Object[fieldCount];
		for(int i = 0 ; i < fieldCount; ++i){
			filedsArray[i] = new Object();
		}
		return fields;
	}
	public static Object[] getUpdateFields(Class<?>doClass){
		Field[] fields = doClass.getDeclaredFields();
		int fieldCount = fields.length - 1;
		//exception primary key
		Object[] filedsArray = new Object[fieldCount];
		for(int i = 0 ; i < fieldCount; ++i){
			filedsArray[i] = new Object();
		}
		return fields;	
	}
	
	public static String   getInsertSql(Class<?>doClass, String tableName){
		StringBuilder insertSql  =  new StringBuilder("insert into  ");
		insertSql.append(tableName);
		insertSql.append("(");
		
		Field[] fields = doClass.getDeclaredFields();
		int fieldCount = fields.length ;
		
		for(int i = 0 ; i < fieldCount; ++i){
			Field filed = fields[i];
			insertSql.append(filed.getName());
			insertSql.append(",");
		}
		insertSql.replace(insertSql.length() - 1, insertSql.length(), ")");
		insertSql.append(" values (");
		for(int i = 0; i < fieldCount; ++i){
			insertSql.append("?,");
		}
		insertSql.replace(insertSql.length() - 1, insertSql.length(), ")");
		 return  insertSql.toString();	
	}
	public static String   getUpdateSql(Class<?>doClass, String tableName){		
		Field[] fields = doClass.getDeclaredFields();
		int fieldCount = fields.length ;
		
		StringBuilder updateSql = new StringBuilder("update ");
		updateSql.append(tableName);
		updateSql.append(" set ");	
		for(int i = 0 ; i < fieldCount; ++i){
			Field filed = fields[i];
			if(filed.getAnnotation(PKey.class) != null)
				continue;
			updateSql.append(filed.getName());
			updateSql.append("= ?,");
		}
		updateSql.deleteCharAt(updateSql.length() - 1);
		updateSql.append(" where ");
		updateSql.append(getPk(doClass));
		updateSql.append(" = ?");
		return  updateSql.toString();	
	}
	public static String   getSelectSql(Class<?>doClass, String tableName){		
		StringBuilder selectSql = new StringBuilder("select * from ");
		selectSql.append(tableName);
		selectSql.append(" where ");
		selectSql.append(getPk(doClass));
		selectSql.append(" = ?");
		return selectSql.toString();	
	}
	public static String   getDeleteSql(Class<?>doClass, String tableName)
	{
		StringBuilder deleteSql = new StringBuilder("delete from ");
		deleteSql.append(tableName);
		deleteSql.append(" where ");
		deleteSql.append(getPk(doClass));
		deleteSql.append(" = ? limit 1");	
		return  deleteSql.toString();
	}

	public static String   getPk(Class<?>doClass){
		Field[] fields = doClass.getDeclaredFields();
		for(int i = 0 ; i < fields.length; ++i){
			Field field = fields[i];
			if(field.getAnnotation(PKey.class) != null)
				return field.getName();
		}
		throw new PrimaryKeyNullException(" do obj primary key not foung should do like @Pkey ");	
	}
}

