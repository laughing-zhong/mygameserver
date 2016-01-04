package com.ares.framework.dao.couchbase;



/**
 * Interface that facilitates applying delta objects to domain objects when using
 * safeUpdate. The applyDelta method manipulates the domain object so that it
 * can be persisted to couchbase. If the write fails due to CAS, a fresh copy of the
 * domain object is read and applyDelta is executed again. This continues until the
 * write goes through or a guard limit is hit.
 *
 */

public interface IUpdateDO<Delta, DO> {
	/**
	 * Takes the deltaObject and applies it to objectToPersist
	 * <p/>
	 * The objectToPersist passed in as a param may or may not be affected by the change.
	 * <p/>
	 * The only guarantee for the new changes to take effect is to refer to the return object.
	 *
	 * @param delta data to facilitate changes to objectToPersist
	 * @param objectToPersist The base object to alter
	 * @return The modified domain object
	 */
	//DO applyDelta( Delta delta, DO objectToPersist ) throws UnableToApplyDeltaException;
	DO applyDelta( Delta delta, DO objectToPersist );
}
