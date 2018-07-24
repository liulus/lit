package com.github.lit.support.event;

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

    Class<?>[] value() default {};

    Class<?>[] classes() default {};

    JoinTime joinTime() default JoinTime.AFTER_RETURN;

    String returnProperty() default "returnValue";

    Type publishType() default Type.SYNC;

    enum Type {
        SYNC,
        ASYNC,;
    }

    enum JoinTime {
        BEFORE,
        AFTER,
        AFTER_RETURN,
    }

}
