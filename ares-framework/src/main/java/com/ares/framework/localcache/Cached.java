package com.ares.framework.localcache;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {
   int cacheCount();
}


