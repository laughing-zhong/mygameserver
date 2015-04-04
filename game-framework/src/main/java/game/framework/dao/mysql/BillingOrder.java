package game.framework.dao.mysql;

import java.sql.Timestamp;
import java.util.Date;

public class BillingOrder {

	private String orderId;
	private int platformType;
	private String uid;
	private String storeItemId;
	private double cost;
	private int status;
	private Timestamp insertTime;
	private Date updateTime;
	private String storeVersion;
	
	
	public BillingOrder() {};
	
	public BillingOrder(String orderId, int platformType, String uid, String storeItemId, int cost, 
			int status, Long timestamp, String storeVersion) {
		this.orderId = orderId;
		this.platformType = platformType;
		this.uid = uid;
		this.storeItemId = storeItemId;
		this.cost = cost;
		this.status = status;
		this.insertTime = new Timestamp(timestamp);
		this.storeVersion = storeVersion;
	}
	
	public BillingOrder(String orderId, int platformType, String uid, String storeItemId, int cost, 
			int status, Long insertTime, String storeVersion, Date updateTime) {
		this.orderId = orderId;
		this.platformType = platformType;
		this.uid = uid;
		this.storeItemId = storeItemId;
		this.cost = cost;
		this.status = status;
		this.insertTime = new Timestamp(insertTime);
		this.storeVersion = storeVersion;
		this.updateTime = updateTime;
	}
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public int getPlatformType() {
		return platformType;
	}
	public void setPlatformType(int platformType) {
		this.platformType = platformType;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getStoreItemId() {
		return storeItemId;
	}
	public void setStoreItemId(String storeItemId) {
		this.storeItemId = storeItemId;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Timestamp getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Timestamp insertTime) {
		this.insertTime = insertTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getStoreVersion() {
		return storeVersion;
	}

	public void setStoreVersion(String storeVersion) {
		this.storeVersion = storeVersion;
	}

}
