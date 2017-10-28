package com.github.lit.commons.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * User : liulu
 * Date : 2017-2-21 18:22
 * version $Id: ApplicationContextUtils.java, v 0.1 Exp $
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {

    private SpringContextUtils(){}

    private static ApplicationContext context;

    private static Environment environment;

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(Class<T> clazz, Object... objects) {
        return context.getBean(clazz, objects);
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static Object getBean(String name, Object... objects) {
        return context.getBean(name, objects);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> clazz) {
        return context.getBeansWithAnnotation(clazz);
    }

    public static String getProperty (String key) {
        return environment.getProperty(key);
    }

    public static String getProperty (String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    public static <T> T getProperty (String key, Class<T> targetType) {
        return environment.getProperty(key, targetType);
    }

    public static <T> T getProperty (String key, Class<T> targetType, T defaultValue) {
        return environment.getProperty(key, targetType, defaultValue);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
        environment = applicationContext.getEnvironment();
    }

    public static ApplicationContext getApplicationContext(){
        return context;
    }

    public static Environment getEnvironment() {
        return environment;
    }
}
