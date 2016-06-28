package com.ares.framework.dao.mysql;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.util.Strings;

import com.ares.service.exception.TableNameNullException;


public class MySqlBaseDAO<T> {
	private Class<?> doClass;
	private String tableName;
	private static Logger logger =LoggerFactory.getLogger(MySqlBaseDAO.class);

	@Inject
	private DbUtilsTemplate  dbUtilsTmpl;

	public MySqlBaseDAO(Class<?> doClass) {
		this.doClass = doClass;
		try {
			initDoMethod();
			initCRUDSql();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	public void initCRUDSql(){
		Table tableEntity = this.getClass().getAnnotation(Table.class);
		if(tableEntity == null 
				|| Strings.isNullOrEmpty(tableEntity.value())){
			throw new TableNameNullException( this.doClass.getSimpleName() +" table is null you should set like @Table(\"table_name\")");
		}
		this.tableName    = tableEntity.value();
		logger.debug("table name:{}", tableName);
		this.selectObjSql = DoSqlUtil.getSelectOneObjSql(doClass, tableName);
		logger.debug("selectObjSql  :{}", selectObjSql);
		this.insertObjSql = DoSqlUtil.getInsertSql(doClass, tableName);	
		logger.debug("insertObjSql  :{}", insertObjSql);
		this.updateObjSql = DoSqlUtil.getUpdateSql(doClass, tableName);
		logger.debug("updateObjSql  :{}", updateObjSql);
		deleteObjSql      = DoSqlUtil.getDeleteSql(doClass, tableName);	
		logger.debug("deleteObjSql  :{}", deleteObjSql);
	}

	@SuppressWarnings("unchecked")
	public T  get(Object key) {
		return (T) dbUtilsTmpl.findFirst(doClass, this.selectObjSql, key);
	}

	
	@SuppressWarnings("unchecked")
	public List<T> getList(Object key) {
		return (List<T>) dbUtilsTmpl.find(doClass, selectObjSql, key);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getList(Object[] keys) {
		return (List<T>) dbUtilsTmpl.find(doClass, selectObjSql, keys);
	}

	public int add(T obj){
		setFiledValues(obj);
		return dbUtilsTmpl.update(insertObjSql, fieldValues);
	}
		
	public int set(T obj){
		setFiledValues(obj);
		return dbUtilsTmpl.update(updateObjSql, fieldValues);
	}
	
	public int delete(Object key){
		return dbUtilsTmpl.update(deleteObjSql, key);
	}
	
	@SuppressWarnings("unchecked")
	public T find(String sql, Class<?> entityClass){	
		return (T) dbUtilsTmpl.findFirst(entityClass, sql);
	}

	public int update(String sql){
		return dbUtilsTmpl.update(sql);
	}
	
	public int update(String sql, Object[] params){
		return dbUtilsTmpl.update(sql, params);
	}
	public int update(String sql ,Object param){
		return dbUtilsTmpl.update(sql, param);
	}
	
	public int[] batchUpdate(String sql, Object[][] params){
		return dbUtilsTmpl.batchUpdate(sql, params);
	}

	private void setFiledValues(T obj) {
		try {
			for (int i = 0; i < dataObjFiledSetClassList.size(); ++i) {
				DoFiledCallClass doFiledSetClass = dataObjFiledSetClassList.get(i);
				fieldValues[i] = doFiledSetClass.getFiledValue(obj);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();

		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}	


	private void initDoMethod() throws NoSuchMethodException, SecurityException {
		Field[] fields = doClass.getDeclaredFields();		
		DoFiledCallClass pkFiledClass = null;
		fieldValues = new Object[fields.length];
		for (int i = 0; i < fields.length; ++i) {
			Field field = fields[i];
			String filedName = field.getName();
			Index pkey = field.getAnnotation(Index.class);
	
			String filedGetMethodName = "get" + toUpperCaseFirstOne(filedName);
			Method fiedGetMethod      = doClass.getDeclaredMethod(filedGetMethodName);
			
			DoFiledCallClass doFiledSetClass = new DoFiledCallClass(filedName, fiedGetMethod);
			if(pkey != null){
				pkFiledClass = doFiledSetClass;
				continue;
			}
			dataObjFiledSetClassList.add(doFiledSetClass);
		}
		dataObjFiledSetClassList.add(pkFiledClass);
	}

	private String insertObjSql;
	private String updateObjSql;
	private String selectObjSql;
	private String deleteObjSql;
	private Object[] fieldValues;

	
	
	public static String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder())
					.append(Character.toUpperCase(s.charAt(0)))
					.append(s.substring(1)).toString();
	}
	
	private List<DoFiledCallClass> dataObjFiledSetClassList = new ArrayList<DoFiledCallClass>();

	private class DoFiledCallClass {
		private Method fieldGetMethod;
		public DoFiledCallClass(String filedName, Method filedGetMethod) {
			this.fieldGetMethod = filedGetMethod;
		}

		public Object getFiledValue(T obj) 
				throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			return fieldGetMethod.invoke(obj);
		}
	}
}
