package game.framework.dal.couchbase.transaction;

import game.framework.dao.couchbase.ICasCouchbaseDAO;
import game.framework.dao.couchbase.IUpdateMultiOpt;
import game.framework.domain.json.JsonDO;


public interface Cb2PcTranactionService {
	public <DO1 extends JsonDO, DO2 extends JsonDO, Delta1, Delta2> boolean startTransaction(
			Delta1 delta1, Delta2 delta2, ICasCouchbaseDAO<DO1> srcDAO,
			String targetId1, ICasCouchbaseDAO<DO2> destDAO, String targetId2,
			IUpdateMultiOpt<Delta1, DO1, Delta2, DO2> callable);

}
