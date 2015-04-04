package game.framework.domain.json;

import game.framework.domain.KeyedDO;

public abstract class ViewJsonDO extends KeyedDO<String> {
	public abstract void setKey( String key );
	public abstract void setValue( String value );
}
