package com.github.lit.support.web;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User : liulu
 * Date : 2018/4/16 18:50
 * version $Id: EnableLitWeb.java, v 0.1 Exp $
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({WebSupportConfigure.class})
public @interface EnableLitWeb {
}
