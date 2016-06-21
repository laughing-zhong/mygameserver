package com.ares.web.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;




@Component
public class AuthenticationService implements UserDetailsService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger( AuthenticationService.class );



	@Transactional
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
 
		
		return null;
	}
}