package com.github.lit.commons.exception;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * User : liulu
 * Date : 2017/6/18 16:35
 * version $Id: EnableExceptionHandlerCondition.java, v 0.1 Exp $
 */
public class EnableExceptionHandlerCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String property = conditionContext.getEnvironment().getProperty("lit.exception.advice.enable", "true");
        return Boolean.valueOf(property);
    }
}
