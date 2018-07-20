package com.github.lit.spring.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

/**
 * User : liulu
 * Date : 2018/3/24 11:19
 * version $Id: OnClassCondition.java, v 0.1 Exp $
 */
public class OnClassCondition implements Condition{

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata metadata) {

        MultiValueMap<String, Object> attributes = getAttributes(metadata, ConditionalOnClass.class);
        if (attributes == null) {
            return true;
        }

        for (Object matchClasses : attributes.get("value")) {
            for (String matchClass : (String[]) matchClasses) {
                if (!ClassUtils.isPresent(matchClass, conditionContext.getClassLoader())) {
                    return false;
                }
            }
        }

        for (Object matchClasses : attributes.get("name")) {
            for (String matchClass : (String[]) matchClasses) {
                if (!ClassUtils.isPresent( matchClass, conditionContext.getClassLoader())) {
                    return false;
                }
            }
        }

        return true;

    }

    private MultiValueMap<String, Object> getAttributes(AnnotatedTypeMetadata metadata, Class<?> annotationType) {
        return metadata.getAllAnnotationAttributes(annotationType.getName(), true);
    }
}
