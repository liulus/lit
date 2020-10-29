package com.lit.support.data.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liulu
 * @version 1.0
 * created_at 2020/5/11
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface LitRepository {

    @AliasFor(annotation = Component.class)
    String value() default "";

    /**
     * with the repository, used jdbcTemplate bean id
     * @return String
     */
    String dataSource() default "";
}
