package game.framework.dal.couchbase;

import java.io.IOException;

import javax.annotation.PreDestroy;

import game.framework.dao.couchbase.transcoder.JsonObjectMapper;
import game.framework.domain.json.JsonDO;
import game.framework.util.JsonUtil;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.view.View;


public class CloseableCouchbaseClientImpl implements CloseableCouchbaseClient{
	
	private final Bucket bucket;
	private final Cluster cluster;
	public CloseableCouchbaseClientImpl(CouchbaseConnectionConfigBean  config){
		
		CouchbaseEnvironment env = DefaultCouchbaseEnvironment
			    .builder()
			    .computationPoolSize(5)
			    .connectTimeout(20000)
			    .build();
		this.cluster = CouchbaseCluster.create(env,config.getNodes());
		this.bucket = cluster.openBucket(config.getBucket(), config.getPassword());	
	}
	
	@PreDestroy
	public void preDestroy() {
		if (this.cluster != null) {
			this.cluster.disconnect();
		}
	}

	@Override
	public boolean delete(String targetId) {
		bucket.remove(targetId);
		return false;
	}

	@Override
	public View getView(String doc, String view) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(String targetId, JsonDO jsonObj) {
		String jsonStr = JsonUtil.genJsonStr(jsonObj);
         if(jsonStr != null){
        	 RawJsonDocument doc = RawJsonDocument.create(targetId,jsonStr);
        	 bucket.upsert(doc);
         }
	}

	@Override
	public void add(JsonDO jsonObj) {
//		String jsonStr = JsonUtil.genJsonStr(jsonObj);
//        if(jsonStr != null){
//       	 RawJsonDocument doc = RawJsonDocument.create(targetId,jsonStr);
//       	 bucket.upsert(doc);
//        }
//		
	}

	@Override
	public void replace(String targetId, JsonDO jsonObj) {
		String jsonStr = JsonUtil.genJsonStr(jsonObj);
        if(jsonStr != null){
       	 RawJsonDocument doc = RawJsonDocument.create(targetId,jsonStr);
       	 bucket.upsert(doc);
        }	
	}

	@Override
	public void set(String targetId, int expire, JsonDO jsonObj) {
		String jsonStr = JsonUtil.genJsonStr(jsonObj);
        if(jsonStr != null){
       	 RawJsonDocument doc = RawJsonDocument.create(targetId,expire,jsonStr);
       	 bucket.upsert(doc);
        }		
	}

	@Override
	public void set(String targetId, JsonDO jsonObj) {
		replace(targetId,jsonObj);
	}

	@Override
	public <T extends JsonDO> T get(String targetId, Class<T> objClass) {
		String content =  bucket.get(targetId, RawJsonDocument.class).content();
		try {
			return JsonObjectMapper.getInstance().readValue(content, objClass);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
