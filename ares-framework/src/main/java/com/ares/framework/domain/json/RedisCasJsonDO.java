package com.ares.framework.domain.json;

public class RedisCasJsonDO extends JsonDO{
	private long cas;

	public void setCas( long cas ) {
		this.cas = cas;
	}

	public long getCas() {
		return cas;
	}

}
