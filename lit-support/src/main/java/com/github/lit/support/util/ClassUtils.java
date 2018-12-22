package com.github.lit.support.util;

import com.github.lit.support.exception.SysException;
import org.springframework.util.TypeUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * 反射工具类
 * @author liulu
 * date 2016-10-2 9:48
 * @see org.springframework.util.ClassUtils
 */
public class ClassUtils {

    /**
     * Suffix for array class names: "[]"
     */
    public static final String ARRAY_SUFFIX = "[]";

    /**
     * Prefix for internal array class names: "["
     */
    private static final String INTERNAL_ARRAY_PREFIX = "[";

    /**
     * Prefix for internal non-primitive array class names: "[L"
     */
    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

    /**
     * The package separator character: '.'
     */
    private static final char PACKAGE_SEPARATOR = '.';

    /**
     * The path separator character: '/'
     */
    private static final char PATH_SEPARATOR = '/';

    /**
     * The inner class separator character: '$'
     */
    private static final char INNER_CLASS_SEPARATOR = '$';

    /**
     * The CGLIB class separator: "$$"
     */
    public static final String CGLIB_CLASS_SEPARATOR = "$$";

    /**
     * The ".class" file suffix
     */
    public static final String CLASS_FILE_SUFFIX = ".class";

    /**
     * Map with primitive wrapper type as key and corresponding primitive
     * type as value, for example: Integer.class -> int.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);

    /**
     * Map with primitive type as key and corresponding wrapper
     * type as value, for example: int.class -> Integer.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(8);

    /**
     * Map with primitive type name as key and corresponding primitive
     * type as value, for example: "int" -> "int.class".
     */
    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<String, Class<?>>(32);

    /**
     * Map with common "java.lang" class name as key and corresponding Class as value.
     * Primarily for efficient deserialization of remote invocations.
     */
    private static final Map<String, Class<?>> commonClassCache = new HashMap<String, Class<?>>(32);


    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);

        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
            registerCommonClasses(entry.getKey());
        }

        Set<Class<?>> primitiveTypes = new HashSet<Class<?>>(32);
        primitiveTypes.addAll(primitiveWrapperTypeMap.values());
        primitiveTypes.addAll(Arrays.asList(boolean[].class, byte[].class, char[].class, double[].class,
                float[].class, int[].class, long[].class, short[].class));
        primitiveTypes.add(void.class);
        for (Class<?> primitiveType : primitiveTypes) {
            primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
        }

        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class,
                Float[].class, Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class,
                Object.class, Object[].class, Class.class, Class[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class,
                Error.class, StackTraceElement.class, StackTraceElement[].class);
    }

    /**
     * Register the given common classes with the ClassUtils cache.
     */
    private static void registerCommonClasses(Class<?>... commonClasses) {
        for (Class<?> clazz : commonClasses) {
            commonClassCache.put(clazz.getName(), clazz);
        }
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
            throw new SysException("Method 调用失败 " + method.getName(), e);
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
     * Determine whether the {@link Class} identified by the supplied name is present
     * and can be loaded. Will return {@code false} if either the class or
     * one of its dependencies is not present or cannot be loaded.
     *
     * @param className the name of the class to check
     *                  (may be {@code null}, which indicates the default class loader)
     * @return whether the specified class is present
     */
    public static boolean isPresent(String className) {
        try {
            forName(className);
            return true;
        } catch (Throwable ex) {
            // Class or one of its dependencies is not present...
            return false;
        }
    }

    /**
     * 加载类
     *
     * @param name class
     * @return Class对象
     */
    public static Class<?> forName(String name) {
        return forName(name, null);
    }


    public static Class<?> forName(String name, ClassLoader classLoader) {
        Assert.notNull(name, "Name must not be null");

        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz == null) {
            clazz = commonClassCache.get(name);
        }
        if (clazz != null) {
            return clazz;
        }

        // "java.lang.String[]" style arrays
        if (name.endsWith(ARRAY_SUFFIX)) {
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[Ljava.lang.String;" style arrays
        if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
            String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[[I" or "[[Ljava.lang.String;" style arrays
        if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
            String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        ClassLoader clToUse = classLoader;
        if (clToUse == null) {
            clToUse = getDefaultClassLoader();
        }
        try {
            return (clToUse != null ? clToUse.loadClass(name) : Class.forName(name));
        } catch (ClassNotFoundException ex) {
            int lastDotIndex = name.lastIndexOf(PACKAGE_SEPARATOR);
            if (lastDotIndex != -1) {
                String innerClassName =
                        name.substring(0, lastDotIndex) + INNER_CLASS_SEPARATOR + name.substring(lastDotIndex + 1);
                try {
                    return (clToUse != null ? clToUse.loadClass(innerClassName) : Class.forName(innerClassName));
                } catch (ClassNotFoundException ex2) {
                    // Swallow - let original exception get through
                }
            }
            throw new SysException("load class error " + name, ex);
        }
    }

    public static Class<?> resolvePrimitiveClassName(String name) {
        Class<?> result = null;
        // Most class names will be quite long, considering that they
        // SHOULD sit in a package, so a length check is worthwhile.
        if (name != null && name.length() <= 8) {
            // Could be a primitive - likely.
            result = primitiveTypeNameMap.get(name);
        }
        return result;
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
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
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

    public static boolean isSimpleValueType(Class<?> clazz) {
        return isPrimitiveOrWrapper(clazz) || clazz.isEnum() || CharSequence.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz) || URI.class == clazz || URL.class == clazz || Locale.class == clazz || Class.class == clazz;
    }

    /**
     * Check if the right-hand side type may be assigned to the left-hand side
     * type, assuming setting by reflection. Considers primitive wrapper
     * classes as assignable to the corresponding primitive types.
     * @param lhsType the target type
     * @param rhsType the value type that should be assigned to the target type
     * @return if the target type is assignable from the value type
     * @see TypeUtils#isAssignable
     */
    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        Assert.notNull(lhsType, "Left-hand side type must not be null");
        Assert.notNull(rhsType, "Right-hand side type must not be null");
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }
        if (lhsType.isPrimitive()) {
            Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
            if (lhsType == resolvedPrimitive) {
                return true;
            }
        }
        else {
            Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
            if (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert a "/"-based resource path to a "."-based fully qualified class name.
     * @param resourcePath the resource path pointing to a class
     * @return the corresponding fully qualified class name
     */
    public static String convertResourcePathToClassName(String resourcePath) {
        Assert.notNull(resourcePath, "Resource path must not be null");
        return resourcePath.replace(PATH_SEPARATOR, PACKAGE_SEPARATOR);
    }

    /**
     * Check whether the specified class is a CGLIB-generated class.
     *
     * @param clazz the class to check
     * @return boolean
     */
    public static boolean isCglibProxyClass(Class<?> clazz) {
        return (clazz != null && isCglibProxyClassName(clazz.getName()));
    }

    /**
     * Check whether the specified class name is a CGLIB-generated class.
     *
     * @param className the class name to check
     * @return boolean
     */
    public static boolean isCglibProxyClassName(String className) {
        return (className != null && className.contains(CGLIB_CLASS_SEPARATOR));
    }

}
