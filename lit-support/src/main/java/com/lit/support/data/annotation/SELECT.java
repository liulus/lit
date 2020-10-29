package com.lit.support.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liulu
 * @version V1.0
 * @since 2020/10/9
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SELECT {

    String value() default "";

    String sql() default "";

    Class<?> type() default Object.class;

    String method() default "";

}
