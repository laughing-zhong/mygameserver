package com.ares.app.helper;


/**
 * @author wesley
 */


public interface AuthorizationHelper {

	void isAuthorizedDebugMode();
	//void isAuthorizedPort( int restrictPort, int comparePort );
	void isAuthorizedPort( String restrictPort, int comparePort );

}
