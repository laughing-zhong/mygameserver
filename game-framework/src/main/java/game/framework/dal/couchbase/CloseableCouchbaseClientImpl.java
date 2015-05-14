package game.framework.dal.couchbase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import rx.Observable;
import game.framework.dal.couchbase.transaction.CbTransaction;
import game.framework.dal.couchbase.transaction.CbTransaction.TsState;
import game.framework.dao.couchbase.IUpdateDO;
import game.framework.dao.couchbase.IUpdateMultiOpt;
import game.framework.dao.couchbase.transcoder.JsonObjectMapper;
import game.framework.domain.json.CasJsonDO;
import game.framework.domain.json.JsonDO;
import game.framework.msg.publish.EventPublisher;
import game.framework.util.JsonUtil;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.PersistTo;
import com.couchbase.client.java.ReplicateTo;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.error.CASMismatchException;
import com.couchbase.client.java.view.View;
import com.fasterxml.jackson.core.JsonProcessingException;


public class CloseableCouchbaseClientImpl implements CloseableCouchbaseClient{
	
	private final AsyncBucket asynBucket;
	private final Bucket   blockBucket;
	private final Cluster  cluster;
	private final EventPublisher  eventPublisher;
	public CloseableCouchbaseClientImpl(CouchbaseConnectionConfigBean  config,EventPublisher  eventPublisher){
		
		CouchbaseEnvironment env = DefaultCouchbaseEnvironment
			    .builder()
			    .connectTimeout(20000)
			    .build();
		this.cluster = CouchbaseCluster.create(env,config.getNodes());
		blockBucket = cluster.openBucket(config.getBucket(), config.getPassword());	
		this.asynBucket  = blockBucket.async();
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
		return true;
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
        	 this.asynCreate(targetId,doc);
         }
	}
	
	@Override
	public void replace(String targetId, JsonDO jsonObj) {
		String jsonStr = JsonUtil.genJsonStr(jsonObj);
        if(jsonStr != null){
        	if(jsonObj instanceof CasJsonDO){
        		RawJsonDocument  doc = RawJsonDocument.create(targetId,jsonStr,((CasJsonDO) jsonObj).getCas());
       	        asynReplace(targetId,doc);
        	}
        	else{
        		RawJsonDocument doc = RawJsonDocument.create(targetId,jsonStr);	
        		asynUpdate(targetId,doc);
        	}
        }
	}
	
	@Override
	public void set(String targetId, int expire, JsonDO jsonObj) {
		String jsonStr = JsonUtil.genJsonStr(jsonObj);
        if(jsonStr != null){
        	if(jsonObj instanceof CasJsonDO){
        		RawJsonDocument  doc = RawJsonDocument.create(targetId,jsonStr,((CasJsonDO) jsonObj).getCas());
       	        asynReplace(targetId,doc);
        	}
        	else{
        		RawJsonDocument doc = RawJsonDocument.create(targetId,jsonStr);	
        		asynUpdate(targetId,doc);
        	}
        }
	}
	
	
	private void asynReplace(String targetId,RawJsonDocument doc){
		 asynBucket.replace(doc)
    	 .timeout(10000, TimeUnit.MILLISECONDS)
    	 .onErrorReturn(throwable -> {  
       		this.onError(targetId, doc,throwable);
			return null;
         })
         .subscribe();
	}
	
	private void asynUpdate(String targetId,RawJsonDocument doc){
		asynCreate(targetId,doc);
	}

	
	private void asynCreate(String targetId,RawJsonDocument doc){
		asynBucket.upsert(doc)
    	 .timeout(10000, TimeUnit.MILLISECONDS)
    	 .onErrorReturn(throwable -> { 
       		this.onError(null, doc,throwable);
			return null;
         })
         .subscribe();	
	}
	
	private void asynDel(String targetId){
	    asynBucket.remove(targetId)
	   	 .timeout(10000, TimeUnit.MILLISECONDS)
	   	 .onErrorReturn(throwable -> {  		 
       		 this.onError(targetId, "",throwable);
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
		RawJsonDocument  document = blockBucket.get(targetId, RawJsonDocument.class);
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
		eventPublisher.publisDaoError(targetId, doc.content());
	}
	
	private void onError(String targetId,String doc,Throwable  throwable){
		eventPublisher.publisDaoError(targetId, doc);
		throwable.printStackTrace();
	}
	
	private void onError(String targetId,Object obj,Throwable  throwable){
		try {
			String strObject = JsonObjectMapper.getInstance().writeValueAsString(obj);
			onError(targetId,strObject,throwable);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	@Override
	public <Delta, DO extends JsonDO> void safeUpdate(String targetId,Delta delta,Class<DO> domainClass, IUpdateDO<Delta, DO> callable) {
			Observable
					.defer(() -> asynBucket.get(targetId,RawJsonDocument.class))
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
					.flatMap(asynBucket::replace)
					.retryWhen(
							attempts -> attempts.flatMap(n -> {
								if (!(n instanceof CASMismatchException)) {
									onError(targetId,delta,n);
									return Observable.error(n.getCause());
								}
								return Observable.timer(1, TimeUnit.SECONDS);
							})).subscribe();
		}

	@Override
	public boolean safeSave(String targetId, JsonDO jsonObj) {
		String content = JsonUtil.genJsonStr(jsonObj);
		RawJsonDocument doc = RawJsonDocument.create(targetId,content);
		try{
		RawJsonDocument  savedDoc = blockBucket.upsert(doc, PersistTo.MASTER,ReplicateTo.ONE,10,TimeUnit.SECONDS);
		if(savedDoc.equals(doc)) return true;
		else   return false;	
		}catch(RuntimeException e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean lock(String targetId,int seconds) {
		try{
		RawJsonDocument  doc = this.blockBucket.getAndLock(targetId, seconds,RawJsonDocument.class);
		if(doc == null)  return false;
		} catch(RuntimeException e){
			e.printStackTrace();
			return  false;
		}
		return true;
	}

	@Override
	public <DO extends JsonDO> List<DO> getByIds(List<String> targetIds) {
		
//		List<DO>  docs = new ArrayList<DO>(targetIds.size());
//		final CountDownLatch latch = new CountDownLatch(targetIds.size());
//		Observable.range(0, targetIds.size())
//		.subscribe(new Action1<RawJsonDocument>() {
//		        @Override
//		        public void call(RawJsonDocument doc) {
//		            latch.countDown();
//		        }
//		    });
//
//		latch.await();
//		return docs;
		return null;
	}

	@Override
	public <DeltaData1, DO1 ,DeltaData2, DO2> boolean commitTransaction(
			String targetId, DO1 domainSrc, DO2 domainDest,
			DeltaData1 deltaData1, DeltaData2 deltaData2,
			IUpdateMultiOpt<DeltaData1, DO1, DeltaData2, DO2> callable,
			CbTransaction transaction) {

		if (transaction.getState() != TsState.INIT) {
			throw new RuntimeException("the trasaction state is error  should "
					+ TsState.INIT);
		}

		callable.applyDo1Delta(deltaData1, domainSrc);
		callable.applyDo2Delta(deltaData2, domainDest);
		try {
			String daltaStr = JsonObjectMapper.getInstance().writeValueAsString(deltaData1);
			transaction.setSrc(daltaStr);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			// step 1 create and set transaction to couchbase
			updateCbTransaction(targetId, transaction);

			// step 2 update 2 DO

			// first data
			this.blockUpsert(targetId, domainSrc);
			transaction.setState(TsState.SRC_PENDING);
			updateCbTransaction(targetId, transaction);

			// second data
			this.blockUpsert(targetId, domainDest);
			transaction.setState(TsState.DEST_PENDING);
			updateCbTransaction(targetId, transaction);

			// step 3 committed
			transaction.setState(TsState.COMMITED);
			updateCbTransaction(targetId, transaction);
		} catch (RuntimeException e) {
			rollback(targetId,deltaData1,domainSrc,deltaData2,domainDest,callable,transaction);
			return false;
		}
		return true;
	}
	
	
	private <DeltaData1, DeltaData2, DO1, DO2> void  rollback(String targetId, DeltaData1 delta1,DO1 domainSrc,DeltaData2 delta2, DO2 domainDest,
			IUpdateMultiOpt<DeltaData1, DO1, DeltaData2, DO2> callable,CbTransaction transaction){
		switch(transaction.getState()){
		case SRC_PENDING:
			callable.revertDo1Delta(delta1, domainSrc);
			this.blockUpsert(targetId, domainSrc);
			break;
		case DEST_PENDING://the data have put to db
			
			break;
		case COMMITED:
			break;
			default:
			break;
		}
		
		
	}
	
	private void  updateCbTransaction(String targetId,CbTransaction  transaction){
      	String jsonStr = JsonUtil.genJsonStr(transaction); 	
      	 RawJsonDocument doc =  RawJsonDocument.create(targetId,30,jsonStr,0);	
	     this.blockBucket.upsert(doc, 10, TimeUnit.SECONDS);
	}
	
	private <DO> void blockUpsert(String targetId,DO  jsonDo){
        String content1 = null;
		try {
			content1 = JsonObjectMapper.getInstance().writeValueAsString(jsonDo);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        RawJsonDocument doc1 = RawJsonDocument.create(targetId,content1);
        this.blockBucket.upsert(doc1);
	}


	
}
