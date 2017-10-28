package com.github.lit.jdbc.spring.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User : liulu
 * Date : 2017/3/15 15:50
 * version $Id: EnableLitJdbc.java, v 0.1 Exp $
 */
@Target(TYPE)
@Retention(RUNTIME)
@Import(JdbcToolsConfig.class)
public @interface EnableLitJdbc {

}
