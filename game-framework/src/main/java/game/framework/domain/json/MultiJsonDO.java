package game.framework.domain.json;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiDomainObject is a simple object that holds all the idList of type SingleCouchbaseDomainObject.
 * Adding and removing idList from this objects and saving it lets it persist to the DAo layer.
 *
 * @param <DO>
 * @author Brian
 */

public abstract class MultiJsonDO<DO extends JsonDO> extends JsonDO {
	private List<String> idList = new ArrayList<>();

	public MultiJsonDO() {}

	public void setIdList( List<String> idList ) {
		this.idList = idList;
	}

	public List<String> getIdList() {
		return idList;
	}

	/**
	 * Wrapper function around append(String id)
	 * @param object The JsonDO to add.  It will take the id from this object add to the list.
	 * @return true if the append was successful
	 */
	public boolean append( DO object ) {
		return append( object.getId() );
	}

	/**
	 * Adds the id to this list in MultiDomainObject
	 *
	 * @param id The id of the SingleCouchbaseDomainObject to add.
	 * @return
	 */
	public boolean append( String id ) {
		return idList.add( id );
	}

	public boolean remove( DO object ) {
		return remove( object.getId() );
	}

	public boolean remove( String id ) {
		return idList.remove( id );
	}
}
