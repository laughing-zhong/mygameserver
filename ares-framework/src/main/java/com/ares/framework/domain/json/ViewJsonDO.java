package com.ares.framework.domain.json;

import com.ares.framework.domain.KeyedDO;

public abstract class ViewJsonDO extends KeyedDO<String> {
	public abstract void setKey( String key );
	public abstract void setValue( String value );
}
