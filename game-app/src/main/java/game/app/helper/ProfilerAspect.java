package game.app.helper;

import game.framework.util.ProfilerUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.perf4j.LoggingStopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

/**
 * @author dadler
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
