package game.framework.dal.couchbase;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import rx.Observable;
import rx.functions.Func1;
import game.framework.dao.couchbase.transcoder.JsonObjectMapper;
import game.framework.domain.json.JsonDO;
import game.framework.msg.publish.EventPublisher;
import game.framework.util.JsonUtil;

import com.couchbase.client.deps.io.netty.handler.timeout.TimeoutException;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.ReplicaMode;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.view.View;


public class CloseableCouchbaseClientImpl implements CloseableCouchbaseClient{
	
	private final AsyncBucket bucket;
	private final Cluster cluster;
	private final EventPublisher  eventPublisher;
	public CloseableCouchbaseClientImpl(CouchbaseConnectionConfigBean  config,EventPublisher  eventPublisher){
		
		CouchbaseEnvironment env = DefaultCouchbaseEnvironment
			    .builder()
			    .computationPoolSize(5)
			    .connectTimeout(20000)
			    .build();
		this.cluster = CouchbaseCluster.create(env,config.getNodes());
		this.bucket = cluster.openBucket(config.getBucket(), config.getPassword()).async();	
		this.eventPublisher =eventPublisher;
	}
	
	@PreDestroy
	public void preDestroy() {
		if (this.cluster != null) {
			this.cluster.disconnect();
		}
	}

	@Override
	public boolean delete(String targetId) {
		asynDel(targetId);
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
        	 this.asynWrite(doc);
         }
	}



	@Override
	public void replace(String targetId, JsonDO jsonObj) {
		String jsonStr = JsonUtil.genJsonStr(jsonObj);
        if(jsonStr != null){
       	 RawJsonDocument doc = RawJsonDocument.create(targetId,jsonStr);
         asynWrite(doc);
        }
	}

	@Override
	public void set(String targetId, int expire, JsonDO jsonObj) {
		String jsonStr = JsonUtil.genJsonStr(jsonObj);
        if(jsonStr != null){
       	 RawJsonDocument doc = RawJsonDocument.create(targetId,expire,jsonStr);
       	 asynWrite(doc);
        }		
	}
	
	private void asynWrite(RawJsonDocument doc){
    	 bucket.upsert(doc)
    	 .timeout(30000, TimeUnit.MILLISECONDS)
    	 .onErrorReturn(throwable -> {  		 
       		this.onError(null, doc);
			return null;
         });
	}
	
	private void asynDel(String targetId){
	   	 bucket.remove(targetId)
	   	 .timeout(30000, TimeUnit.MILLISECONDS)
	   	 .onErrorReturn(throwable -> {  		 
       		 this.onError(targetId, null);
			 return null;
         });
    
	}

	@Override
	public void set(String targetId, JsonDO jsonObj) {
		replace(targetId,jsonObj);
	}

	@Override
	public <T extends JsonDO> T get(String targetId, Class<T> objClass) {
		RawJsonDocument  document = bucket.get(targetId, RawJsonDocument.class).toBlocking().singleOrDefault(null);
		if(document == null) return null;
		try {
			return JsonObjectMapper.getInstance().readValue(document.content(), objClass);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void onError(String targetId,RawJsonDocument doc){
		eventPublisher.publisDaoError(targetId, doc.toString());
	}
	
}
