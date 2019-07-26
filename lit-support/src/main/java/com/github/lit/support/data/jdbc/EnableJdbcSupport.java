package com.github.lit.support.data.jdbc;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-22 13:18
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({JdbcSupportConfigure.class})
public @interface EnableJdbcSupport {
}
