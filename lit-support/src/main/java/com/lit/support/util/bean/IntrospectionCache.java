package com.lit.support.util.bean;

import com.lit.support.exception.SysException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * JavaBean信息缓存
 * User : liulu
 * Date : 2016-10-6 9:51
 */
public class IntrospectionCache {

    /**
     * Map keyed by class containing IntrospectionCache.
     * Needs to be a WeakHashMap with WeakReferences as values to allow
     * for proper garbage collection in case of multiple class loaders.
     */
    private static final Map<Class<?>, Object> classCache = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * 类的属性信息，key为属性名
     */
    private final Map<String, PropertyDescriptor> propertyDescriptorCache;

    /**
     * Instantiates a new Introspection cache.
     *
     * @param beanClass the other class
     */
    private IntrospectionCache(Class<?> beanClass) {

        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);

            // 从Introspector缓存立即移除类，在类加载终止时允许适当的垃圾收集
            // 我们不管如何总是缓存在这里,这是一个GC友好的方式，对比于IntrospectionCache，
            // Introspector没有使用弱引用作为WeakHashMap的值
            Class<?> classToFlush = beanClass;
            while (classToFlush != null) {
                Introspector.flushFromCaches(classToFlush);
                classToFlush = classToFlush.getSuperclass();
            }
            this.propertyDescriptorCache = new LinkedHashMap<>();

            // This call is slow so we do it once.
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor pd : pds) {
                if (Class.class.equals(beanClass) && "classLoader".equals(pd.getName())
                        ||  "protectionDomain".equals(pd.getName())) {
                    // Ignore Class.getClassLoader() and getProtectionDomain() methods - nobody needs to bind to those
                    continue;
                }
                this.propertyDescriptorCache.put(pd.getName(), pd);
            }
        } catch (IntrospectionException ex) {
            throw new SysException("初始化缓存bean信息时出现异常", ex);
        }
    }

    /**
     * For class.
     *
     * @param beanClass the other class
     * @return the introspection cache
     */
    public static IntrospectionCache forClass(Class<?> beanClass) {

        IntrospectionCache introspectionCache;
        Object value = classCache.get(beanClass);

        if (value == null) {
            introspectionCache = new IntrospectionCache(beanClass);
            classCache.put(beanClass, introspectionCache);
            return introspectionCache;
        }

        if (value instanceof Reference) {
            @SuppressWarnings("rawtypes")
            Reference ref = (Reference) value;
            return (IntrospectionCache) ref.get();
        }

        return (IntrospectionCache) value;
    }

    /**
     * Get property descriptors.
     *
     * @return the property descriptor [ ]
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] pds = new PropertyDescriptor[this.propertyDescriptorCache.size()];
        int i = 0;
        for (PropertyDescriptor pd : this.propertyDescriptorCache.values()) {
            pds[i] = pd;
            i++;
        }
        return pds;
    }

    /**
     * Get property descriptor.
     *
     * @param name the name
     * @return the property descriptor
     */
    public PropertyDescriptor getPropertyDescriptor(String name) {

        PropertyDescriptor pd = this.propertyDescriptorCache.get(name);

        if (pd == null && name != null) {
            // Same lenient fallback checking as in PropertyTypeDescriptor...
            pd = this.propertyDescriptorCache.get(name.substring(0, 1).toLowerCase() + name.substring(1));
            if (pd == null) {
                pd = this.propertyDescriptorCache.get(name.substring(0, 1).toUpperCase() + name.substring(1));
            }
        }
        return pd;
    }

}
