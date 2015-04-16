package game.framework.dal.couchbase;

import java.util.List;

import game.framework.dao.couchbase.IUpdateDO;
import game.framework.domain.json.CasJsonDO;
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
	/**
	 * delete or remove one item from couchabse
	 * @param targetId
	 * @return
	 */
	public boolean delete(String targetId);
	public View getView(String doc,String view);
	/**
	 * create new item is the targetId not exist should use "create" do not use other API to write the couchabse
	 * @param targetId
	 * @param jsonObj
	 */
	public void create(String targetId,JsonDO jsonObj);
	/**
	 * replace and set both to replace the targetId item it will use CAS 
	 * that to say one get one set not allowed to one get two or more write
	 * @param targetId
	 * @param jsonObj
	 */
	public void replace(String targetId,JsonDO  jsonObj);
	public void set(String targetId, int expire,JsonDO jsonObj);
	public void set(String targetId,JsonDO jsonObj);
	
	
	public <T extends JsonDO> T  get(String targetId,Class<T>  objClass);
	/**
	 * it will try many time to set the dalta data to the object until it sucess
	 * @param targetId
	 * @param deltaObject
	 * @param object
	 * @param updateDo
	 */
	public <DeltaObject, DO extends JsonDO> void safeUpdate(String targetId, DeltaObject deltaObject,Class<DO> domainObjectClass, IUpdateDO<DeltaObject, DO> callable);
	
	
	/**
	 * it will observe the data persistance and replicate 
	 * notice: this method is slow so only used for the importance data
	 */
	
	public boolean safeSave(String targetId,JsonDO jsonObj);
	
	/**
	 * lockTime the time to write lock the document (max. 30 seconds).
	 * @param targetId
	 * @param seconds
	 * @return
	 */
	public boolean  lock(String targetId,int seconds);
	
	public <DO extends JsonDO> List<DO> getByIds(List<String> targetIds);

}
