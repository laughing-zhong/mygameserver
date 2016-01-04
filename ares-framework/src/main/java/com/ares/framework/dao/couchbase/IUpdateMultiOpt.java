package com.ares.framework.dao.couchbase;

public interface IUpdateMultiOpt<Delta1, DO1 ,Delta2 ,DO2>  {
	
	public void applyDo1Delta(Delta1 delta, DO1 do1 );
	public void applyDo2Delta(Delta2 delta, DO2 do2);
	public void revertDo1Delta(Delta1 delta1, DO1 do1);
	public void reverDo2Dleta(Delta2 delta2,DO2 doDest);

}
