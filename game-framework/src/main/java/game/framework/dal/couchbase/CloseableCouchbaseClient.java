package game.framework.dal.couchbase;

import game.framework.domain.json.JsonDO;

import com.couchbase.client.java.view.View;




/**
 * CouchbaseClient that implements the AutoCloseable interface. This subclass of {@link com.couchbase.client.CouchbaseClient}
 * allows use of try-with-resources and {@link com.ea.game.framework.dal.couchbase.CouchbasePoolDataSource}.getConnection().
 * <p/>
 * This class is intended to be used along with an object pool to allow automatic retrieval/return of objects to the pool.
 *
 * @author wesley
 */

public interface CloseableCouchbaseClient  {
	
	public boolean delete(String targetId);
	public View getView(String doc,String view);
	public void add(String targetId,JsonDO jsonObj);
	public void replace(String targetId,JsonDO  jsonObj);
	public void set(String targetId, int expire,JsonDO jsonObj);
	public void set(String targetId,JsonDO jsonObj);
	public <T extends JsonDO> T  get(String targetId,Class<T>  objClass);

}
