package game.framework.dao.couchbase.impl;


import game.framework.dal.couchbase.CouchbaseDataSource;
import game.framework.dao.exception.DAOException;
import game.framework.domain.json.ViewJsonDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




import java.util.List;


public abstract class CouchbaseViewDAO<DomainObject extends ViewJsonDO> {

	private static final Logger LOGGER = LoggerFactory.getLogger( CouchbaseViewDAO.class );

	private Class<DomainObject> domainObjectClass;
	private CouchbaseDataSource dataSource;

	public CouchbaseViewDAO( CouchbaseDataSource dataSource, Class<DomainObject> domainObjectClass ) {
		this.dataSource = dataSource;
		this.domainObjectClass = domainObjectClass;
	}

	protected abstract String getDesignDocumentName();
	protected abstract String getViewName();
	protected abstract String getMapFunction();

	public List<DomainObject> getAll() throws DAOException {
//        CloseableCouchbaseClient client = dataSource.getConnection() ;
//			List<DomainObject> results = new ArrayList<>();
//			View view;
//			
//				view = client.getView( getDesignDocumentName(), getViewName() );
//		
//				// If the view isn't created yet, then create it now and try retrieving it again.
//				createView();
//				view = client.getView( getDesignDocumentName(), getViewName() );
//			
//			Query query = new Query();
//			query.setStale( Stale.FALSE );
//			ViewResponse result = client.query( view, query );
//			for ( ViewRow row : result ) {
//				try {
//					DomainObject domainObject = domainObjectClass.newInstance();
//					domainObject.setKey( row.getKey() );
//					domainObject.setValue( row.getValue() );
//					results.add( domainObject ); // deal with the document/data
//				} catch ( InstantiationException | IllegalAccessException e ) {
//					LOGGER.error( "Unable to instantiate a new domain object.", e );
//				}
//			}
//			return results;
//		}
		return null;
	}

	private void createView() throws DAOException {
//		try ( CloseableCouchbaseClient client = dataSource.getConnection() ) {
//			DesignDocument<Object> designDoc = new DesignDocument<>( getDesignDocumentName() );
//			designDoc.getViews().add( new ViewDesign( getViewName(), getMapFunction() ) );
//			client.createDesignDoc( designDoc );
//		}
	}
}
