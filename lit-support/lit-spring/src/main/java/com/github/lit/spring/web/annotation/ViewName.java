package com.github.lit.spring.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User : liulu
 * Date : 2018/3/1 16:29
 * version $Id: ViewName.java, v 0.1 Exp $
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewName {

    String value();

    /**
     * 是否是spring el 表达式
     *
     * @return boolean
     */
    boolean spel() default false;
}
