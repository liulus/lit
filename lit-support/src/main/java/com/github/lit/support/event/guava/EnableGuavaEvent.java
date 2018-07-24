package com.github.lit.support.event.guava;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * User : liulu
 * Date : 2018/3/24 11:15
 * version $Id: EnableGuavaEvent.java, v 0.1 Exp $
 */
@Target(TYPE)
@Retention(RUNTIME)
@Import(GuavaEventConfig.class)
public @interface EnableGuavaEvent {
}
