package com.ares.app.helper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.perf4j.LoggingStopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

import com.ares.framework.util.ProfilerUtils;

/**
 * @author wesley
 */
public class ProfilerAspect {

	public Object invoke( ProceedingJoinPoint pjp ) throws Throwable {

		LoggingStopWatch stopWatch = new Slf4JStopWatch( ProfilerUtils.getInvocationKey( pjp ) );

		try {
			return pjp.proceed();

		} finally {
			stopWatch.stop();
		}

	}

}
