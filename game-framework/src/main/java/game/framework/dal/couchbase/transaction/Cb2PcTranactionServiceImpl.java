package game.framework.dal.couchbase.transaction;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import game.framework.dal.couchbase.transaction.CbTransaction.TsState;
import game.framework.dao.couchbase.ICasCouchbaseDAO;
import game.framework.dao.couchbase.IUpdateMultiOpt;
import game.framework.dao.couchbase.transcoder.JsonObjectMapper;
import game.framework.domain.json.JsonDO;


@Component
public class Cb2PcTranactionServiceImpl implements Cb2PcTranactionService {
	
	
	@Inject
	private CbTransactionDAO  transactionDAO;

	@Override
	public <DO1 extends JsonDO, DO2 extends JsonDO, Delta1, Delta2> boolean startTransaction(
			Delta1 delta1, Delta2 delta2,
			ICasCouchbaseDAO<DO1> srcDAO, String targetId1,
			ICasCouchbaseDAO<DO2> destDAO, String targetId2,
			IUpdateMultiOpt<Delta1, DO1, Delta2, DO2> callable) {
	
		DO1 do1 = srcDAO.findById(targetId1);
		callable.applyDo1Delta(delta1, do1);
		
		//change do2
		DO2 do2 = destDAO.findById(targetId2);
		callable.applyDo2Delta(delta2, do2);
		
		CbTransaction transaction = new CbTransaction();
	
		
		transaction.setSrcId(targetId1);
		transaction.setDestId(targetId2);
		try {
			String deltaSrcStr = JsonObjectMapper.getInstance().writeValueAsString(delta1);
			transaction.setSrcDelta(deltaSrcStr);
			String  deltaDestStr = JsonObjectMapper.getInstance().writeValueAsString(delta2);
			transaction.setDestDelta(deltaDestStr);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			// step 1 create and set transaction to couchbase
			transaction.setState(TsState.INIT);
			this.transactionDAO.put(transaction);

			// step 2 update 2 DO

			// first data
			srcDAO.put(do1);
			transaction.setState(TsState.SRC_PENDING);
			this.transactionDAO.put(transaction);;

			// second data
			destDAO.put(do2);
			transaction.setState(TsState.DEST_PENDING);
			this.transactionDAO.put(transaction);;

			// step 3 committed
			transaction.setState(TsState.COMMITED);
			this.transactionDAO.put(transaction);
			
		} catch (RuntimeException e) {
			rollback(targetId1,targetId2,delta1,do1,delta2,do2,srcDAO,destDAO,callable,transaction);
			return false;
		}
		return true;

	}
	
	private <DeltaData1, DeltaData2, DO1 extends JsonDO, DO2 extends JsonDO> void rollback(String targetId1,String targetId2,
			DeltaData1 delta1, DO1 domainSrc, DeltaData2 delta2,
			DO2 domainDest,
			ICasCouchbaseDAO<DO1> srcDAO,
			ICasCouchbaseDAO<DO2> destDAO,
			IUpdateMultiOpt<DeltaData1, DO1, DeltaData2, DO2> callable,
			CbTransaction transaction) {
		switch(transaction.getState()){
		case SRC_PENDING:
			callable.revertDo1Delta(delta1, domainSrc);
			srcDAO.put(domainSrc);
			break;
		case DEST_PENDING://the data have put to db
			callable.revertDo1Delta(delta1, domainSrc);
			callable.reverDo2Dleta(delta2, domainDest);
			srcDAO.put(domainSrc);
			destDAO.put(domainDest);	
			break;
		case COMMITED: //transaction has finished all changed data save to db but the transaction state not save sucess
			break;
			default:
			break;
		}	
	}

}
