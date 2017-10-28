package com.github.lit.commons.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User : liulu
 * Date : 2017/8/9 21:52
 * version $Id: Event.java, v 0.1 Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Event {

    Class<?> eventClass();

    Type publishType() default Type.ASYNC;

    enum Type {
        SYNC,
        ASYNC,;
    }
}
