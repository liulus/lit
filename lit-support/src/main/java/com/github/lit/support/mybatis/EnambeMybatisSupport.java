package com.github.lit.support.mybatis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liulu
 * @version v1.0
 * date 2018-12-22 13:51
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({MyBatisSupportConfigure.class})
public @interface EnambeMybatisSupport {
}
