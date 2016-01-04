package com.ares.framework.dal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation that specifies DAO key prefix and suffix
 * <p/>
 * See com.ea.vanguard.framework.dao.couchbase.impl.CouchbaseDAO for more info.*
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface EntityKey {
	String value() default "";
	String prefix() default "";
	String suffix() default "";
}
