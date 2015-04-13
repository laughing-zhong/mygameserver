package game.app.content.vip;

import game.framework.domain.json.CasJsonDO;


public class VipConfigDO extends CasJsonDO {
	public VipConfigDO(){}
	private VipConfig  vipConfig;
	
	public VipConfig getVipConfig() {
		return vipConfig;
	}

	public void setVipConfig(VipConfig vipConfig) {
		this.vipConfig = vipConfig;
	}

	public VipConfigDO(String targetId, VipConfig vipConfig){
		//this.setId(targetId);
		this.vipConfig = vipConfig;
	}

}
