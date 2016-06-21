package com.ares.web.content.controller;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Filter allows us to restrict access to the /services url for the configured port.
 * 
 * @author m.mcbride
 *
 */
public class PortProcessingFilter extends GenericFilterBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(PortProcessingFilter.class);
	
	@Value("${services.port.restriction:8080}")
	private Integer servicePortRestriction;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		if (httpRequest.getPathInfo() != null 
				&& httpRequest.getPathInfo().startsWith("/services") && httpRequest.getPathInfo().startsWith("/admin")
				&& request.getLocalPort() != servicePortRestriction){
			LOGGER.warn("Rejecting request on port/path: {} {}", request.getLocalPort(), httpRequest.getPathInfo());
			throw new IllegalArgumentException("Admin services are unreachable on the current port.");
		}
		chain.doFilter(request, response);
	}


}
