package com.ares.framework.domain;

/**
 * Interface that identifies a class as Identifiable with respect
 * to a getId()/setId() pair.
 *
 * @author wesley
 */

public interface Identifiable<KeyType> {
	KeyType getId();
	void setId( KeyType id );
}
