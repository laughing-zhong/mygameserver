package game.framework.dal.couchbase;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import rx.Observable;
import game.framework.dao.couchbase.IUpdateDO;
import game.framework.dao.couchbase.transcoder.JsonObjectMapper;
import game.framework.domain.json.CasJsonDO;
import game.framework.domain.json.JsonDO;
import game.framework.msg.publish.EventPublisher;
import game.framework.util.JsonUtil;
import game.service.exception.IlligleDataException;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.error.CASMismatchException;
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
	public void create(String targetId, JsonDO jsonObj) {
		String jsonStr = JsonUtil.genJsonStr(jsonObj);
         if(jsonStr != null){
        	 RawJsonDocument doc = RawJsonDocument.create(targetId,jsonStr);
        	 this.asynCreate(doc);
         }
	}



	@Override
	public void replace(String targetId, JsonDO jsonObj) {
		String jsonStr = JsonUtil.genJsonStr(jsonObj);
        if(jsonStr != null){
        	if(jsonObj instanceof CasJsonDO){
        		RawJsonDocument  doc = RawJsonDocument.create(targetId,jsonStr,((CasJsonDO) jsonObj).getCas());
       	      asynReplace(doc);
        	}
        	else{
        		RawJsonDocument doc = RawJsonDocument.create(targetId,jsonStr);	
        		asynUpdate(doc);
        	}
        }
	}
	
	@Override
	public void set(String targetId, int expire, JsonDO jsonObj) {
		String jsonStr = JsonUtil.genJsonStr(jsonObj);
        if(jsonStr != null){
        	if(jsonObj instanceof CasJsonDO){
        		RawJsonDocument  doc = RawJsonDocument.create(targetId,jsonStr,((CasJsonDO) jsonObj).getCas());
       	        asynReplace(doc);
        	}
        	else{
        		RawJsonDocument doc = RawJsonDocument.create(targetId,jsonStr);	
        		asynUpdate(doc);
        	}
        }
	}
	
	
	private void asynReplace(RawJsonDocument doc){
		 bucket.replace(doc)
    	 .timeout(10000, TimeUnit.MILLISECONDS)
    	 .onErrorReturn(throwable -> {  
       		this.onError(null, doc,throwable);
			return null;
         })
         .subscribe();
	}
	
	private void asynUpdate(RawJsonDocument doc){
		asynCreate(doc);
	}

	
	private void asynCreate(RawJsonDocument doc){
		 bucket.upsert(doc)
    	 .timeout(10000, TimeUnit.MILLISECONDS)
    	 .onErrorReturn(throwable -> { 
       		this.onError(null, doc,throwable);
			return null;
         })
         .subscribe();
	}
	
	private void asynDel(String targetId){
	   	 bucket.remove(targetId)
	   	 .timeout(10000, TimeUnit.MILLISECONDS)
	   	 .onErrorReturn(throwable -> {  		 
       		 this.onError(targetId, null,throwable);
			 return null;
         })
         .subscribe();
    
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
			 T  obj = JsonObjectMapper.getInstance().readValue(document.content(), objClass);
			 if( obj instanceof CasJsonDO)
				((CasJsonDO) obj).setCas(document.cas());
			 return obj;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void onError(String targetId,RawJsonDocument doc, Throwable  throwable){
		throwable.printStackTrace();
		eventPublisher.publisDaoError(targetId, doc.toString());
	}


	@Override
	public <Delta, DO extends JsonDO> void safeUpdate(String targetId,Delta delta,Class<DO> domainClass, IUpdateDO<Delta, DO> callable) {
			Observable
					.defer(() -> bucket.get(targetId,RawJsonDocument.class))
					.map(document -> {		     
						try {
							DO object = JsonObjectMapper.getInstance().readValue(document.content(),domainClass);
							DO updatedObject = callable.applyDelta(delta,object);
							String jsonStr = JsonUtil.genJsonStr(updatedObject);
							return RawJsonDocument.from(document ,targetId, jsonStr);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					})
					.flatMap(bucket::replace)
					.retryWhen(
							attempts -> attempts.flatMap(n -> {
								if (!(n instanceof CASMismatchException)) {
									return Observable.error(n.getCause());
								}
								return Observable.timer(1, TimeUnit.SECONDS);
							})).subscribe();
		}


}
