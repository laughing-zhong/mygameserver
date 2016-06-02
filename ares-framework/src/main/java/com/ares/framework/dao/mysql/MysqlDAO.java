package com.ares.framework.dao.mysql;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Timestamp;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.testng.util.Strings;

import com.ares.service.exception.TableNameNullException;


public class MysqlDAO<T> {
	private Class<?> doClass;
	private String tableName;
	private JdbcTemplate jdbcTemplate;

	private static Map<String, Class<?>> methodNames = new HashMap<String, Class<?>>();
	private static Map<Class<?>, Method> methodMap = new HashMap<Class<?>, Method>();
	static {
		methodNames.put("getString", String.class);
		methodNames.put("getInt", int.class);
		methodNames.put("getTimestamp", Timestamp.class);
		methodNames.put("getDate", Date.class);
		methodNames.put("getLong", Long.class);
		Iterator<String> mdIter = methodNames.keySet().iterator();
		try {
			while (mdIter.hasNext()) {
				String methodName = mdIter.next();
				Class<?> classType = methodNames.get(methodName);
				Method method = ResultSet.class.getMethod(methodName, String.class);
				methodMap.put(classType, method);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public MysqlDAO(Class<?> doClass) {
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
		this.tableName = tableEntity.value();

		//--------------------------select sql-----------------------------------
		this.selectObjSql = DoSqlUtil.getSelectOneObjSql(doClass, tableName);
		this.selectObjListSql = DoSqlUtil.getSelectObjListSql(doClass, tableName);		
		//------------------- insert sql--------------------------------------------
		this.insertObjSql = DoSqlUtil.getInsertSql(doClass, tableName);	
		//------------------------------------------------ update ---------------------------
		this.updateObjSql = DoSqlUtil.getUpdateSql(doClass, tableName);
		//-------------------------------------------------------  delete------------------------
		deleteObjSql = DoSqlUtil.getDeleteSql(doClass, tableName);	
	}
	@Resource(name = "dataSource")
	public void setDataSource(DataSource ds) {
		jdbcTemplate = new JdbcTemplate(ds);
	}

	public T get(String key) {
		return this.jdbcTemplate.queryForObject(this.selectObjSql, customerRowMapper, key);
	}
	public T get(int key) {
		return this.jdbcTemplate.queryForObject(this.selectObjSql, customerRowMapper, key);
	}
	
	public List<T> getList(int key) {
		return this.jdbcTemplate.query(selectObjListSql, customerRowMapper, key);
	}

	public List<T> getList(String key) {
		return this.jdbcTemplate.query(selectObjListSql, customerRowMapper, key);
	}
	public int add(T obj){
		setFiledValues(obj);
		return this.jdbcTemplate.update(this.insertObjSql, fieldValues);
	}
	
	
	//update for sql
	public int set(T obj){
		setFiledValues(obj);
		return this.jdbcTemplate.update(this.updateObjSql, fieldValues);
	}
	
	public int delete(String key){
		return this.jdbcTemplate.update(deleteObjSql, key);
	}
	public int delete(int key){
		return this.jdbcTemplate.update(this.deleteObjSql, key);
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
			PKey pkey = field.getAnnotation(PKey.class);
				
			String fieldSetMethodName = "set" + toUpperCaseFirstOne(filedName);
			Method fiedSetMethod = doClass.getDeclaredMethod(fieldSetMethodName,field.getType());
			String filedGetMethodName = "get" + toUpperCaseFirstOne(filedName);
			Method fiedGetMethod = doClass.getDeclaredMethod(filedGetMethodName);
			
			Method rrsMethod = methodMap.get(field.getType());
			DoFiledCallClass doFiledSetClass = new DoFiledCallClass(filedName,fiedSetMethod, fiedGetMethod, rrsMethod);
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
	private String selectObjListSql;
	private String deleteObjSql;
	private Object[] fieldValues;

	
	private List<DoFiledCallClass> dataObjFiledSetClassList = new ArrayList<DoFiledCallClass>();
	private CustomRowMapper customerRowMapper = new CustomRowMapper();

	private class CustomRowMapper implements RowMapper<T> {
		@Override
		public T mapRow(ResultSet rs, int rowNum) throws SQLException {

			try {
				@SuppressWarnings("unchecked")
				T objInstance = (T) doClass.newInstance();
				for (int i = 0; i < dataObjFiledSetClassList.size(); ++i) {
					DoFiledCallClass dofiledSetClass = dataObjFiledSetClassList.get(i);
					dofiledSetClass.setFiledValue(objInstance, rs);
				}
				return objInstance;

			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private class DoFiledCallClass {
		private String fieldName;
		private Method fieldSetMethod;
		private Method fieldGetMethod;
		private Method rrsMethod;

		public DoFiledCallClass(String filedName, Method filedSetMethod, Method filedGetMethod,
				Method rrsMethod) {
			this.fieldName = filedName;
			this.fieldSetMethod = filedSetMethod;
			this.fieldGetMethod = filedGetMethod;
			this.rrsMethod = rrsMethod;
		}

		public void setFiledValue(T obj, ResultSet rs)
				throws IllegalAccessException, IllegalArgumentException,
				InvocationTargetException {
			fieldSetMethod.invoke(obj, rrsMethod.invoke(rs, fieldName));
		}
		public Object getFiledValue(T obj) 
				throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
			return fieldGetMethod.invoke(obj);
		}
	}

	public static String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder())
					.append(Character.toUpperCase(s.charAt(0)))
					.append(s.substring(1)).toString();
	}
}
