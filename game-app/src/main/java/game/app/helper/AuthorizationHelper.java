package game.app.helper;


/**
 * @author dadler
 */


public interface AuthorizationHelper {

	void isAuthorizedDebugMode();
	//void isAuthorizedPort( int restrictPort, int comparePort );
	void isAuthorizedPort( String restrictPort, int comparePort );

}
