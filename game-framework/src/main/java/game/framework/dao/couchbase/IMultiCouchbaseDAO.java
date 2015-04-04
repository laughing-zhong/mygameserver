package game.framework.dao.couchbase;

import game.framework.dao.exception.DAOException;
import game.framework.dao.exception.KeyNotFoundException;
import game.framework.domain.json.JsonDO;
import game.framework.domain.json.MultiJsonDO;

import java.util.List;

/**
 * Interface describing the contract for MultiDAO objects. MultiDomainObject maintains a list of
 * keys that refer to other documents within couchbase. Using a MultiJsonDO, collections of larger
 * objects can be managed via a single interface but behind the scenes the documents are stored in
 * separate couchbase records.
 *
 * @author wesley
 */
public interface IMultiCouchbaseDAO<MultiDomainObject extends MultiJsonDO<DomainObject>, DomainObject extends JsonDO> {

	void create( MultiDomainObject obj ) throws DAOException;
	void replace( MultiDomainObject obj ) throws KeyNotFoundException, DAOException;
	void put( MultiDomainObject obj ) throws DAOException;
	void append( MultiDomainObject list, DomainObject obj ) throws DAOException;
	void append( MultiDomainObject list, String newId ) throws DAOException;
	MultiDomainObject findById( String id ) throws DAOException;
	List<MultiDomainObject> findByIds( List<String> ids ) throws DAOException;

}
