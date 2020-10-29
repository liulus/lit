package com.lit.support.data.annotation;

import com.lit.support.data.Logic;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User : liulu
 * Date : 2018/7/11 18:18
 * version $Id: LogicCondition.java, v 0.1 Exp $
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Condition {

    String property() default "";

    Logic logic() default Logic.EQ;


}
