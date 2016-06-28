/**
 *
 * Copyright 2016 
 * All right  reserved	
 * Created on 2016年6月22日 
 */
package com.ares.framework.dao.mysql;
/**
 *@author wesley E-mail:wiqi.zhong@gmail.com
 *
 */
import java.sql.SQLException; 
import java.util.ArrayList; 
import java.util.List; 
import java.util.Map; 

import javax.sql.DataSource; 




import org.apache.commons.dbutils.QueryRunner; 
import org.apache.commons.dbutils.handlers.BeanHandler; 
import org.apache.commons.dbutils.handlers.BeanListHandler; 
import org.apache.commons.dbutils.handlers.MapHandler; 
import org.apache.commons.dbutils.handlers.MapListHandler; 
import org.apache.commons.dbutils.handlers.ScalarHandler; 
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory; 

public class DbUtilsTemplate { 

    private DataSource dataSource; 
    private QueryRunner queryRunner; 
    private static final Log LOG = LogFactory.getLog(DbUtilsTemplate.class); 

    public void setDataSource(DataSource dataSource) { 
        this.dataSource = dataSource; 
    } 

    /** 
     *
     * @param sql 
     * @return
     */ 
    public int update(String sql) { 
        return update(sql, null); 
    } 
      
    /** 
     *
     * <code> 
     * executeUpdate("update user set username = 'kitty' where username = ?", "hello kitty"); 
     * </code> 
     * @param sql sql
     * @param param 
     * @return 
     */ 
    public int update(String sql, Object param) { 
        return update(sql, new Object[] { param }); 
    } 
      
    /** 
     * 
     * @param params 
     * @return 
     */ 
    public int update(String sql, Object[] params) { 
        queryRunner = new QueryRunner(dataSource); 
        int affectedRows = 0; 
        try { 
            if (params == null) { 
                affectedRows = queryRunner.update(sql); 
            } else { 
                affectedRows = queryRunner.update(sql, params); 
            } 
        } catch (SQLException e) { 
            LOG.error("Error occured while attempting to update data", e); 
        } 
        return affectedRows; 
    } 
      
    /** 
     * @param sql sql
     * @param params 
     * @return 
     */ 
    public int[] batchUpdate(String sql, Object[][] params) { 
        queryRunner = new QueryRunner(dataSource); 
        int[] affectedRows = new int[0]; 
        try { 
            affectedRows = queryRunner.batch(sql, params); 
        } catch (SQLException e) { 
            LOG.error("Error occured while attempting to batch update data", e); 
        } 
        return affectedRows; 
    }     

    /** 

     * @param sql
     * @return 
     */ 
    public List<Map<String, Object>> find(String sql) { 
        return find(sql, null); 
    } 
      
    /** 
     * @param sql
     * @param param
     * @return
     */ 
    public List<Map<String, Object>> find(String sql, Object param) { 
        return find(sql, new Object[] {param}); 
    } 
      
    /** 
     * @param sql sql
     * @param params 
     * @return  
     */ 
    public List<Map<String, Object>> find(String sql, Object[] params) { 
        queryRunner = new QueryRunner(dataSource); 
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>(); 
        try { 
            if (params == null) { 
                list = (List<Map<String, Object>>) queryRunner.query(sql, new MapListHandler()); 
            } else { 
                list = (List<Map<String, Object>>) queryRunner.query(sql, new MapListHandler(), params); 
            } 
        } catch (SQLException e) { 
            LOG.error("Error occured while attempting to query data", e); 
        } 
        return list; 
    } 
      
    /** 
     * @param entityClass 类
     * @param sql
     * @return
     */ 
    public <T> List<T> find(Class<T> entityClass, String sql) { 
        return find(entityClass, sql, null); 
    } 
      
    /**  
     * @param entityClass 
     * @param sql sq 
     * @param param
     * @return  
     */ 
    public <T> List<T> find(Class<T> entityClass, String sql, Object param) { 
        return find(entityClass, sql, new Object[] { param }); 
    } 
      
    /** 
     * @param entityClass  
     * @param sql  
     * @param params
     * @return 
     */ 
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> List<T> find(Class<T> entityClass, String sql, Object[] params) { 
        queryRunner = new QueryRunner(dataSource); 
        List<T> list = new ArrayList<T>(); 
        try { 
            if (params == null) { 
                list = (List<T>) queryRunner.query(sql, new BeanListHandler(entityClass)); 
            } else { 
                list = (List<T>) queryRunner.query(sql, new BeanListHandler(entityClass), params); 
            } 
        } catch (SQLException e) { 
            LOG.error("Error occured while attempting to query data", e); 
        } 
        return list; 
    } 
      
    /** 
     * @param entityClass
     * @param sql
     * @return  
     */ 
    public <T> T findFirst(Class<T> entityClass, String sql) { 
        return findFirst(entityClass, sql, null); 
    } 
      
    /** 
     * @param entityClass 
     * @param sql 
     * @param param
     * @return  
     */ 
    public <T> T findFirst(Class<T> entityClass, String sql, Object param) { 
        return findFirst(entityClass, sql, new Object[] { param }); 
    } 
      
    /** 
     * @param entityClass  
     * @param sql sql 
     * @param params 
     * @return 
     */ 
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> T findFirst(Class<T> entityClass, String sql, Object[] params) { 
        queryRunner = new QueryRunner(dataSource); 
        Object object = null; 
        try { 
            if (params == null) { 
                object = queryRunner.query(sql, new BeanHandler(entityClass)); 
            } else { 
                object = queryRunner.query(sql, new BeanHandler(entityClass), params); 
            } 
        } catch (SQLException e) { 
            LOG.error("Error occured while attempting to query data", e); 
        } 
        return (T) object; 
    } 
      
    /** 
     * @param sql sql 
     * @return 
     */ 
    public Map<String, Object> findFirst(String sql) { 
        return findFirst(sql, null); 
    } 
      
    /** 
     * @param sql  
     * @param param 
     * @return
     */ 
    public Map<String, Object> findFirst(String sql, Object param) { 
        return findFirst(sql, new Object[] { param }); 
    } 
      
    /** 
     * @param sql
     * @param params 
     * @return
     */ 

    public Map<String, Object> findFirst(String sql, Object[] params) { 
        queryRunner = new QueryRunner(dataSource); 
        Map<String, Object> map = null; 
        try { 
            if (params == null) { 
                map = (Map<String, Object>) queryRunner.query(sql, new MapHandler()); 
            } else { 
                map = (Map<String, Object>) queryRunner.query(sql, new MapHandler(), params); 
            } 
        } catch (SQLException e) { 
            LOG.error("Error occured while attempting to query data", e); 
        } 
        return map; 
    } 
      
    /** 
     * @param sql  
     * @param columnName 
     * @return 
     */ 
    public Object findBy(String sql, String columnName) { 
        return findBy(sql, columnName, null); 
    } 
      
    /** 
     * @param sql
     * @param columnName 
     * @param param
     * @return  
     */ 
    public Object findBy(String sql, String columnName, Object param) { 
        return findBy(sql, columnName, new Object[] { param }); 
    } 
      
    /** 
     * @param sql  
     * @param columnName 
     * @param params 
     * @return 
     */ 
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Object findBy(String sql, String columnName, Object[] params) { 
        queryRunner = new QueryRunner(dataSource); 
        Object object = null; 
        try { 
            if (params == null) { 
                object = queryRunner.query(sql, new ScalarHandler(columnName)); 
            } else { 
                object = queryRunner.query(sql, new ScalarHandler(columnName), params); 
            } 
        } catch (SQLException e) { 
            LOG.error("Error occured while attempting to query data", e); 
        } 
        return object; 
    } 
      
    /** 
     * @param sql 
     * @param columnIndex 
     * @return
     */ 
    public Object findBy(String sql, int columnIndex) { 
        return findBy(sql, columnIndex, null); 
    } 
      
    /**  
     * @param sql 
     * @param columnIndex 
     * @param param 
     * @return  
     */ 
    public Object findBy(String sql, int columnIndex, Object param) { 
        return findBy(sql, columnIndex, new Object[] { param }); 
    } 
      
    /** 
     * @param sql 
     * @param columnIndex
     * @param params
     * @return 
     */ 
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Object findBy(String sql, int columnIndex, Object[] params) { 
        queryRunner = new QueryRunner(dataSource); 
        Object object = null; 
        try { 
            if (params == null) { 
                object = queryRunner.query(sql, new ScalarHandler(columnIndex)); 
            } else { 
                object = queryRunner.query(sql, new ScalarHandler(columnIndex), params); 
            } 
        } catch (SQLException e) { 
            LOG.error("Error occured while attempting to query data", e); 
        } 
        return object; 
    } 
}