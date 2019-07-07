package com.example.demo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TechlogRetryer {
    long waitMsec() default 0;
    Class[] retryThrowable() default {};
    long maxDelayMsec() default 0;
    int maxAttempt() default 0;
    boolean result() default true;
}
