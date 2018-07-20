package com.github.lit.spring.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * User : liulu
 * Date : 2018/3/24 11:20
 * version $Id: ConditionalOnClass.java, v 0.1 Exp $
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnClassCondition.class)
public @interface ConditionalOnClass {

    Class<?>[] value() default {};

    String[] name() default {};
}
