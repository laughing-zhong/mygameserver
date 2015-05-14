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
	
		public String src;
		public String getSrc() {
			return src;
		}
		public void setSrc(String src) {
			this.src = src;
		}
		public String getDest() {
			return dest;
		}
		public void setDest(String dest) {
			this.dest = dest;
		}
		public TsState getState() {
			return state;
		}
		public void setState(TsState state) {
			this.state = state;
		}
		public String dest;
		public TsState state;
		public CbTransaction(){
			state = TsState.INIT;

	}
}


