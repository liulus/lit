package com.github.lit.commons.util;

import com.github.lit.commons.exception.SysException;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 反射工具类
 * User : liulu
 * Date : 2016-10-2 9:48
 */
public class ClassUtils {

    /**
     * Map with primitive wrapper type as key and corresponding primitive
     * type as value, for example: Integer.class -> int.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<Class<?>, Class<?>>(8);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);
    }


    /**
     * 获取指定对象的属性值
     *
     * @param field 属性字段
     * @param obj   指定对象
     * @return 属性在指定对象的值
     */
    public static Object getFieldValue(Field field, Object obj) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new SysException("获取对象的属性值失败: " + field.getName(), e);
        }
    }

    /**
     * 设置指定对象的属性值
     *
     * @param field 属性字段
     * @param obj   指定对象
     * @param value 要设置的值
     */
    public static void setFieldValue(Field field, Object obj, Object value) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new SysException("设置对象的属性值失败：" + field.getName(), e);
        }
    }

    /**
     * 执行方法
     *
     * @param method 要执行的方法
     * @param obj    执行方法的对象
     * @param value  执行方法的参数
     * @return 方法的返回值
     */
    public static Object invokeMethod(Method method, Object obj, Object... value) {
        methodAccessible(method);
        try {
            return method.invoke(obj, value);
        } catch (Exception e) {
            throw new SysException("Method 调用失败 " + (method == null ? "null" : method.getName()), e);
        }
    }

    /**
     * 设置method访问权限
     *
     * @param method 方法
     */
    public static void methodAccessible(Method method) {
        if (!Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            method.setAccessible(true);
        }
    }

    /**
     * 初始化实例
     *
     * @param clazz class
     * @param <T>   对象类型
     * @return class实例对象
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new SysException("根据class创建实例失败:" + (clazz == null ? "null" : clazz.getName()), e);
        }
    }

    /**
     * 初始化实例
     *
     * @param clazz class
     * @return class实例对象
     */
    public static Object newInstance(String clazz) {

        try {
            Class<?> loadClass = getDefaultClassLoader().loadClass(clazz);
            return loadClass.newInstance();
        } catch (Exception e) {
            throw new SysException("根据class创建实例失败:" + clazz, e);
        }
    }

    /**
     * 加载类
     *
     * @param clazz class
     * @return Class对象
     */
    public static Class<?> loadClass(String clazz) {
        try {
            return getDefaultClassLoader().loadClass(clazz);
        } catch (Exception e) {
            throw new SysException("根据class名称加载class失败:" + clazz, e);
        }
    }

    /**
     * Return the default ClassLoader
     *
     * @return ClassLoader
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }


    /**
     * Check if the given class represents a primitive wrapper,
     * i.e. Boolean, Byte, Character, Short, Integer, Long, Float, or Double.
     *
     * @param clazz the class to check
     * @return whether the given class is a primitive wrapper class
     */
    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return primitiveWrapperTypeMap.containsKey(clazz);
    }

    /**
     * Check if the given class represents a primitive (i.e. boolean, byte,
     * char, short, int, long, float, or double) or a primitive wrapper
     * (i.e. Boolean, Byte, Character, Short, Integer, Long, Float, or Double).
     *
     * @param clazz the class to check
     * @return whether the given class is a primitive or primitive wrapper class
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

}
