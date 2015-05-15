package game.framework.dal.couchbase.transaction;

import game.framework.domain.json.JsonDO;

public class CbTransaction  extends JsonDO{

	/**
	 * 
	 * @author zhongwq
	 * notice: do not change anything in the CbTransaction class
	 *
	 */
	public enum TsState{
		INIT,
		SRC_PENDING,
		DEST_PENDING,
		COMMITED,
		FINISH
	}
	
		public String srcDelta;
        public String srcId;
        public String getSrcDelta() {
			return srcDelta;
		}
		public void setSrcDelta(String srcDelta) {
			this.srcDelta = srcDelta;
		}
		public String getSrcId() {
			return srcId;
		}
		public void setSrcId(String srcId) {
			this.srcId = srcId;
		}
		public String getDestId() {
			return destId;
		}
		public void setDestId(String destId) {
			this.destId = destId;
		}
		public String getDestDelta() {
			return destDelta;
		}
		public void setDestDelta(String destDelta) {
			this.destDelta = destDelta;
		}
		public void setState(TsState state) {
			this.state = state;
		}

		public String destId;

		public TsState getState() {
			return state;
		}
	
		public String destDelta;
		public TsState state;
		public CbTransaction(){
			state = TsState.INIT;

	}
}


