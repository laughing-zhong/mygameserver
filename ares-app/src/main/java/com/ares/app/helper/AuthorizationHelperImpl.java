package com.ares.app.helper;


import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author wesley
 */

@Component
public class AuthorizationHelperImpl implements AuthorizationHelper {

	private static final String NOT_AUTHORIZED_MESSAGE = "You are not authorized to access this function.";
//
//	@Inject
//	private GameConstantsManager gameConstantsManager;

	public void isAuthorizedDebugMode()
	{
		//if ( !gameConstantsManager.getGameConstants().isDebugMode() ) {
			//throw new UnauthorizedException(NOT_AUTHORIZED_MESSAGE);
		//}
	}
	
	public void isAuthorizedPort( String restrictPort, int comparePort )
	{
		System.out.println("restrictPort = " + restrictPort+ "; comparePort = " + comparePort);
		
		String[] restrictPorts = restrictPort.split(",");
		for(String e : restrictPorts) {
			int temp = Integer.valueOf(e);
			System.out.println(temp);
			if ( comparePort == temp ){
				return;
			}
		}
		
		throw new RuntimeException(NOT_AUTHORIZED_MESSAGE);
	}
}

