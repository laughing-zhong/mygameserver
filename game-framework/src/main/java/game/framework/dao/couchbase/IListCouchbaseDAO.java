package game.framework.dao.couchbase;

import game.framework.dao.exception.DAOException;
import game.framework.dao.exception.KeyNotFoundException;
import game.framework.dao.exception.OutOfDateDomainObjectException;
import game.framework.domain.json.CasJsonDO;

import java.util.List;

/**
 * Stores a list of POJOs in couchbase in a single record.
 * Offers append functions to add new domain objects without touching the old objects.
 * @param <DO> domain object to persist the list of POJOs to
 * @param <Element> the type of POJO this DAO will store
 */
public interface IListCouchbaseDAO<DO extends CasJsonDO, Element> extends ICasCouchbaseDAO<DO> {
	/**
	 * Takes the list of elements and appends it to the list in the database
	 *
	 * @param id       The id to save this to
	 * @param elements The list of elements to persist
	 * @throws DAOException
	 */
	void append( String id, List<Element> elements ) throws DAOException;

	/**
	 * Takes the element and appends it to the list in the database
	 *
	 * @param id      The id to save this to
	 * @param element The element to persist
	 * @throws DAOException
	 */
	void append( String id, Element element ) throws DAOException;

	/**
	 * Clears the list domain object from the database
	 *
	 * @param obj The domain object to clear from the database if nothing changed
	 * @throws DAOException
	 * @throws KeyNotFoundException           If it doesn't exist
	 * @throws OutOfDateDomainObjectException If something else modified it since we last retrieved it
	 */
	void safeClear( DO obj ) throws DAOException, KeyNotFoundException, OutOfDateDomainObjectException;
}
