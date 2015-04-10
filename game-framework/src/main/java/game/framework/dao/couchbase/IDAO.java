package game.framework.dao.couchbase;

import game.framework.dal.couchbase.CloseableCouchbaseClient;
import game.framework.dao.exception.DAOException;
import game.framework.dao.exception.KeyNotFoundException;

import java.util.List;

/**
 * Base interface for Couchbase DAOs.
 *
 * @param <DO> The domain object type this DAO will operate on
 */

public interface IDAO<DO> {
	/**
	 * Creates a T object in the database. If the
	 * object already exists in the database, this will return false.
	 *
	 * @param objectToPersist The DomainObject to create in the database
	 * @throws DAOException If there was any serious issues in the Dao layer, a
	 *                      DaoException will be thrown
	 */
	void create( DO objectToPersist ) throws DAOException;

	/**
	 * Replaces a T object in the database. If the object
	 * does NOT exist in the database, this will return false.
	 *
	 * @param objectToPersist The DomainObject to save to the database
	 * @throws DAOException If there was any serious issues in the Dao layer, a
	 *                      DaoException will be thrown
	 */
	void replace( DO objectToPersist ) throws KeyNotFoundException, DAOException;

	/**
	 * Put a T object in the database. This method does
	 * not care if there is an existing value in the database or not.
	 *
	 * @param objectToPersist The DomainObject to put into the database
	 * @throws DAOException If there was any serious issues in the Dao layer, a
	 *                      DaoException will be thrown
	 */
	void put( DO objectToPersist ) throws DAOException;

	/**
	 * Finds a T object by a given targetId.
	 *
	 * @param targetId The targetId of the object. Used to look up in the database.
	 * @return The T object found, or null if none can be found.
	 * @throws DAOException If there was any serious issues in the Dao layer, a
	 *                      DaoException will be thrown
	 */
	DO findById( String targetId ) throws DAOException;
	DO findById( String... targetIds ) throws DAOException;
	DO findById( CloseableCouchbaseClient client, String targetId ) throws DAOException;
	DO findById( CloseableCouchbaseClient client, String... targetIds ) throws DAOException;

	/**
	 * Finds T objects by targetIds.
	 *
	 * @param ids The list of targetIds to be found. Used to look up in the database.
	 * @return A list of found T objects. If a T object cannot be found, it will
	 *         not be present in the list.
	 * @throws DAOException If there was any serious issues in the Dao layer, a
	 *                      DaoException will be thrown
	 */
	List<DO> findByIds( List<String> ids ) throws DAOException;

	/**
	 * Deletes a T object from the database. Returns if it was successful in its
	 * deletion.
	 *
	 * @param targetObject The object to be deleted.
	 * @return If it actually deleted something out of the database.
	 * @throws DAOException If there was any serious issues in the Dao layer, a
	 *                      DaoException will be thrown
	 */
	boolean delete( DO targetObject ) throws DAOException;
	boolean delete( CloseableCouchbaseClient client, DO targetObject ) throws DAOException;

	/**
	 * Deletes a T object from the database. Returns if it was successful in its
	 * deletion.
	 *
	 * @param targetId The targetId of the object that should be deleted
	 * @return If it actually deleted something out of the database
	 * @throws DAOException If there was any serious issues in the Dao layer, a
	 *                      DaoException will be thrown
	 */
	boolean delete( String targetId ) throws DAOException;
	boolean delete( CloseableCouchbaseClient client, String targetId ) throws DAOException;

}
