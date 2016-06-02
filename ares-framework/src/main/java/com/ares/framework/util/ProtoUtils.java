package com.ares.framework.util;

import com.google.protobuf.BlockingService;
import com.google.protobuf.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author dadler
 */
public abstract class ProtoUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger( ProtoUtils.class );
	// These are protobuf generated method names that we must invoke via reflection
	// to wrap the protobuf service wrapper around our rpc implementations.
	private static final String NEW_BLOCKING_SERVICE_METHOD = "newReflectiveBlockingService";
	private static final String NEW_SERVICE_METHOD = "newReflectiveService";


	public static Service wrapService( final Object impl ) {
		return wrapGenericService( impl, NEW_SERVICE_METHOD );
	}


	public static BlockingService wrapBlockingService( final Object impl ) {
		return wrapGenericService( impl, NEW_BLOCKING_SERVICE_METHOD );
	}


	@SuppressWarnings("unchecked")
	public static <T> T wrapGenericService( final Object impl, final String newServiceMethodName ) {

		T wrapper = null;

		Class<?>[] interfaces = impl.getClass().getInterfaces();
		for ( Class<?> interfaze : interfaces ) {
			if ( interfaze == null ) continue;
			Class<?> clazz = interfaze.getEnclosingClass();
			if ( clazz == null ) continue;

			try {
				Method newServiceMethod = clazz.getMethod( newServiceMethodName, interfaze );
				wrapper = (T)newServiceMethod.invoke( clazz, impl );
				LOGGER.debug( "mapped {} -> {}", impl.toString(), wrapper.toString() );
			} catch (Exception ignored ) {}
		}

		return wrapper;
	}


}
