package com.ares.framework.util;

import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpServerErrorException;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author m.mcbride
 *
 */
public class RetryUtils {

	public static RetryTemplate createRetryTemplate(int maxAttempts, int initialInterval, int multiplier) {
		RetryTemplate retryTemplate = new RetryTemplate();
		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(maxAttempts);
		ExceptionClassifierRetryPolicy exceptionRetryPolicy = new ExceptionClassifierRetryPolicy();
		exceptionRetryPolicy.setPolicyMap(ImmutableMap.<Class<? extends Throwable>, RetryPolicy>builder().put(HttpServerErrorException.class, retryPolicy).build());
		retryTemplate.setRetryPolicy(retryPolicy);
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(initialInterval);
		backOffPolicy.setMultiplier(multiplier);
		retryTemplate.setBackOffPolicy(backOffPolicy);
		return retryTemplate;
	}
	
}
