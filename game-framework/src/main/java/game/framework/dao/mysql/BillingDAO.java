package game.framework.dao.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;



@Component
public class BillingDAO {
	
	private JdbcTemplate jdbcTemplate;
	
	@Resource(name="dataSource")
	public void setDataSource(DataSource ds) {
		jdbcTemplate = new JdbcTemplate(ds);
    }
	
	@Transactional
	public void insert (BillingOrder bo) {
		try{
			StringBuffer sql  = new StringBuffer();
			sql.append("INSERT INTO billinghistory (orderid, platform_type, uid, store_itemid, cost, status, insert_time, store_version) VALUES ( ")
			.append("?, ?, ?, ?, ?, ?, ?, ?")
			.append(" ) ");
	
			jdbcTemplate.update(sql.toString(), bo.getOrderId(), bo.getPlatformType(), 
					bo.getUid(), bo.getStoreItemId(), bo.getCost(), bo.getStatus(), new Timestamp(new Date().getTime()), bo.getStoreVersion());
		}catch(Throwable ex) {
			ex.printStackTrace();
		}
	}

	public List<BillingOrder> selectOrderByTimestamp (Timestamp startTime, Timestamp endTime) {
		return jdbcTemplate.query("select * from billinghistory where insert_time between ? and ?", 
				new CustomRowMapper(), startTime, endTime);
	}
	
	public List<BillingOrder> selectOrderByOrderId (final String orderId, final int platformType) {
		return jdbcTemplate.query("select * from billinghistory where orderid = ? and platform_type = ?", 
				new CustomRowMapper(), orderId, platformType);
	}
	
	public List<BillingOrder> selectAndLockOrderByOrderIdAndPlayerId (final String orderId, final String playerId, final int platformType) {
		return jdbcTemplate.query("select * from billinghistory where orderid = ? and platform_type = ? and uid = ? for update", 
				new CustomRowMapper(), orderId, platformType, playerId);
	}
	
	public List<BillingOrder> selectAndLockOrderByOrderId (final String orderId, final int platformType) {
		return jdbcTemplate.query("select * from billinghistory where orderid = ? and platform_type = ? for update", 
				new CustomRowMapper(), orderId, platformType);
	}
	
	public void updateOrderStatus(final String orderId, final int status, final int platformType) {
		jdbcTemplate.update("update billinghistory set status = ?, update_time = now() where orderid = ? and platform_type = ? ", Integer.valueOf(status), orderId, platformType);
	}
	
	private class CustomRowMapper implements RowMapper<BillingOrder> {
		@Override
		public BillingOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new BillingOrder(rs.getString("orderid"), 
									rs.getInt("platform_type"),
									rs.getString("uid"),
									rs.getString("store_itemid"),
									rs.getInt("cost"),
									rs.getInt("status"),
									rs.getTimestamp("insert_time", Calendar.getInstance()).getTime(),
									rs.getString("store_version"),
									rs.getDate("update_time")
									);
		}
	}
	
}
