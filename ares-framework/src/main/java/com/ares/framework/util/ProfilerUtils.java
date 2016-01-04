package com.ares.framework.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Convenience methods for profiling via Spring-AOP and perf4j.
 * @author dadler
 */
public class ProfilerUtils {

	/**
	 * Convenience method to determine ClassName.MethodName for an AOP invocation.
	 * @param pjp	The ProceedingJoinPoint
	 * @return	A string in ClassName.MethodName form
	 */
	public static String getInvocationKey( ProceedingJoinPoint pjp ) {
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();
		return pjp.getTarget().getClass().getSimpleName() + "." + method.getName();
	}
}
