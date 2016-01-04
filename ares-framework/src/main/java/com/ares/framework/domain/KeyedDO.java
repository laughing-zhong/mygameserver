package com.ares.framework.domain;

/**
 * Extending this class gives other domain objects an id of the specified type.
 *
 * @param <KeyType>
 */

public abstract class KeyedDO<KeyType> implements Identifiable<KeyType> {
	private KeyType id;

	public KeyType getId() {
		return id;
	}

	public void setId( KeyType id ) {
		this.id = id;
	}
}
