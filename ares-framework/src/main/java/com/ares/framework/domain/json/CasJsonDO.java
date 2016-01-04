package com.ares.framework.domain.json;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Superclass for all domain objects that are serialized as json and
 * need CAS (compare and swap, check and set) support. Many of the
 * DAO APIs will only work with descendants of CasJsonDO, as cas
 * is prevalent in the couchbase world.
 *
 * @author dadler
 */

abstract public class CasJsonDO extends JsonDO {
	@JsonIgnore
	private long cas;

	public void setCas( long cas ) {
		this.cas = cas;
	}

	public long getCas() {
		return cas;
	}
}