package game.framework.dao.couchbase;

import java.util.List;

import game.framework.dal.couchbase.transaction.CbTransaction;
import game.framework.dao.exception.DAOException;
import game.framework.dao.exception.KeyNotFoundException;
import game.framework.dao.exception.OutOfDateDomainObjectException;
import game.framework.dao.exception.UnableToApplyDeltaException;
import game.framework.domain.json.JsonDO;


/**
 * Interface for all Couchbase DAOs with CAS functionality.
 *
 * @param <DO> the CasJsonDO subtype this DAO will operate on
 */

public interface ICasCouchbaseDAO<DO extends JsonDO> extends IDAO<DO> {
	/**
	 * safeSave will ensure the data in couchbase database haven't been written
	 * between the SafeSingleCouchbaseDomainObject loading and the saving of said object.
	 * <p/>
	 * If the key in couchbase that it's trying to save to has a different cas
	 * value, this will fail and the SafeSingleCouchbaseDomainObject that was passed to it
	 * is no longer valid. Further safeSave called with that object will continue
	 * to fail.
	 * <p/>
	 * If the key is still the same, the object will be saved and the new cas
	 * value will be loaded and updated to the SafeSingleCouchbaseDomainObject.
	 *
	 * @param objectToPersist The object to save to couchbase
	 * @throws OutOfDateDomainObjectException
	 * @throws KeyNotFoundException
	 * @throws DAOException
	 */
	boolean safeSave( DO objectToPersist );

	/**
	 * Calling safeUpdate would first load the object from couchbase.
	 * It would invoke callable.applyDelta() with delta object onto the object found from the database.
	 * Finally it would call safeSave with the new object.
	 * <p/>
	 * If safeSave throws an OutOfDateDomainObjectException it would call safeUpdate() again.
	 *
	 * @param callable    The callable class to apply the delta to the base object
	 * @param deltaObject The object holding the deltas.
	 * @param targetIds   The id of the object to find and save to the database
	 * @return the modified and persisted domain object
	 * @throws KeyNotFoundException        If it can't find the object in the database
	 * @throws DAOException
	 * @throws UnableToApplyDeltaException If callable.applyDelta() fails for some reason
	 */
	<DeltaObject> void safeUpdate( IUpdateDO<DeltaObject, DO> callable, DeltaObject deltaObject, String targetIds );
	

}
