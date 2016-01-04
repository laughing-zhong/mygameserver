package com.ares.app.content.vip;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class VipConfig {
	public VipConfig(){}

	@XmlElement(name="exp_lvl")
	private VipExpLevel  VipExpLvl;
	
	@XmlElement(name="vip_energy_buy")
	private VipLevelCountCost  vipEneryBuy;
	
	@XmlElement(name="vip_send_sweep_count")
	private VipLevelCountCost  vipSeeepCount;
	
	@XmlElement(name="vip_hero_max")
	private VipLevelCountCost  vipHeroCount;
	
	@XmlElement(name="pit")
	private VipLevelCountCost  vipPitCount;
	
	@XmlElement(name="relice")
	private VipLevelCountCost  vipReliceCount;
	
	@XmlElement(name="ladder_battle")
	private VipLevelCountCost  vipLadderBattleCount;
	
	public VipExpLevel getVipExpLvl() {
		return VipExpLvl;
	}

	public void setVipExpLvl(VipExpLevel vipExpLvl) {
		VipExpLvl = vipExpLvl;
	}

	public VipLevelCountCost getVipEneryBuy() {
		return vipEneryBuy;
	}

	public void setVipEneryBuy(VipLevelCountCost vipEneryBuy) {
		this.vipEneryBuy = vipEneryBuy;
	}

	public VipLevelCountCost getVipSeeepCount() {
		return vipSeeepCount;
	}

	public void setVipSeeepCount(VipLevelCountCost vipSeeepCount) {
		this.vipSeeepCount = vipSeeepCount;
	}

	public VipLevelCountCost getVipHeroCount() {
		return vipHeroCount;
	}

	public void setVipHeroCount(VipLevelCountCost vipHeroCount) {
		this.vipHeroCount = vipHeroCount;
	}

	public VipLevelCountCost getVipPitCount() {
		return vipPitCount;
	}

	public void setVipPitCount(VipLevelCountCost vipPitCount) {
		this.vipPitCount = vipPitCount;
	}

	public VipLevelCountCost getVipReliceCount() {
		return vipReliceCount;
	}

	public void setVipReliceCount(VipLevelCountCost vipReliceCount) {
		this.vipReliceCount = vipReliceCount;
	}

	public VipLevelCountCost getVipLadderBattleCount() {
		return vipLadderBattleCount;
	}

	public void setVipLadderBattleCount(VipLevelCountCost vipLadderBattleCount) {
		this.vipLadderBattleCount = vipLadderBattleCount;
	}

	public VipLevelCountCost getVipConquerCount() {
		return vipConquerCount;
	}

	public void setVipConquerCount(VipLevelCountCost vipConquerCount) {
		this.vipConquerCount = vipConquerCount;
	}



	@XmlElement(name="conquer")
	private VipLevelCountCost  vipConquerCount;
	
	
	@XmlElement(name="vip_stamina_buy")
	private  VipLevelCountCost  vipStaminaCount;
	



	
	public VipLevelCountCost getVipStaminaCount() {
		return vipStaminaCount;
	}

	public void setVipStaminaCount(VipLevelCountCost vipStaminaCount) {
		this.vipStaminaCount = vipStaminaCount;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class VipLevelCountCost{
		public VipLevelCountCost(){}
		@XmlElement(name="lvl_count")
		private VipLevelCount  vipLvlCount;
		
		public VipLevelCount getVipLvlCount() {
			return vipLvlCount;
		}

		public void setVipLvlCount(VipLevelCount vipLvlCount) {
			this.vipLvlCount = vipLvlCount;
		}

		public VipCountCost getVipCost() {
			return vipCost;
		}

		public void setVipCost(VipCountCost vipCost) {
			this.vipCost = vipCost;
		}
		@XmlElement(name="count_cost")
		private VipCountCost  vipCost;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class VipLevelCount {
		public VipLevelCount() {}

		@XmlElement(name = "vip_lvl")
		private List<VipValueCount> gardValueds = new ArrayList<VipValueCount>();

		public List<VipValueCount> getGardValueds() {
			return gardValueds;
		}

		public void setGardValueds(List<VipValueCount> gardValueds) {
			this.gardValueds = gardValueds;
		}

		@XmlAccessorType(XmlAccessType.FIELD)
		public static class VipValueCount {

			public VipValueCount() {
			}

			@XmlAttribute(name = "value")
			private int value;

			public int getValue() {
				return value;
			}

			public void setValue(int value) {
				this.value = value;
			}

			public int getCount() {
				return count;
			}

			public void setCount(int count) {
				this.count = count;
			}

			@XmlAttribute(name = "count")
			private int count;
		}
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class VipExpLevel {
		public VipExpLevel() {}

		@XmlElement(name = "value")
		private List<GardValue> gardValueds = new ArrayList<GardValue>();

		public List<GardValue> getGardValueds() {
			return gardValueds;
		}

		public void setGardValued(List<GardValue> gardValueds) {
			this.gardValueds = gardValueds;
		}

		@XmlAccessorType(XmlAccessType.FIELD)
		public static class GardValue {

			public GardValue() {
			}

			@XmlAttribute(name = "count_max")
			private int countMax;

			public int getCountMax() {
				return countMax;
			}

			public void setCountMax(int countMax) {
				this.countMax = countMax;
			}

			public int getCount() {
				return count;
			}

			public void setCount(int count) {
				this.count = count;
			}

			@XmlAttribute(name = "count")
			private int count;

		}
	}
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class VipCountCost{
		public VipCountCost(){}
		
		@XmlElement(name="count")
		private List<CountCost> countCostList = new ArrayList<CountCost>();

		public List<CountCost> getCountCostList() {
			return countCostList;
		}

		public void setCountCostList(List<CountCost> countCostList) {
			this.countCostList = countCostList;
		}
		
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class  CountCost{
		public CountCost(){}
		
		@XmlAttribute(name="index")
		private int index;
		
		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public int getCost() {
			return cost;
		}

		public void setCost(int cost) {
			this.cost = cost;
		}

		@XmlAttribute(name="cost")
		private int cost;
	}

}
