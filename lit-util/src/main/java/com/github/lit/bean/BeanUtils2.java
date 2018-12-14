package com.github.lit.bean;

import com.github.lit.page.PageList;
import com.github.lit.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author liulu
 * @version v1.0
 * @see org.springframework.beans.BeanUtils
 * date 2018-12-06 22:18
 */
public abstract class BeanUtils2 {

    /**
     * 判断classpath下是否存在spring-beans依赖
     */
    private static final boolean SPRING_PRESENT = ClassUtils.isPresent("org.springframework.beans.BeanUtils");

    public static <S, T> T convert(S source, T target) {
        return convert(source, target, "");
    }

    /**
     * 参考spring属性拷贝, 拓展: 相同属性source的null值不会拷贝到target
     *
     * @param source           source
     * @param target           target
     * @param ignoreProperties ignoreProperties
     * @param <S>              S
     * @param <T>              T
     * @return target
     * @see org.springframework.beans.BeanUtils#copyProperties(java.lang.Object, java.lang.Object, java.lang.Class, java.lang.String...)
     */
    public static <S, T> T convert(S source, T target, String... ignoreProperties) {

        if (target == null || source == null) {
            return target;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(target.getClass());

        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            if (value != null) {
                                writeMethod.invoke(target, value);
                            }
                        } catch (Exception ex) {
                            throw new RuntimeException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
        return target;
    }

    public static <S, T> List<T> convertList(Class<T> tClass, List<S> sList) {
        return convertList(tClass, sList, (BiConsumer<S, T>) null, "");
    }

    public static <S, T> List<T> convertList(Class<T> tClass, List<S> sList, String... ignoreProperties) {
        return convertList(tClass, sList, null, ignoreProperties);
    }

    public static <S, T> List<T> convertList(Class<T> tClass, List<S> sList, BiConsumer<S, T> biConsumer) {
        return convertList(tClass, sList, biConsumer, "");
    }

    /**
     * @param tClass           tClass
     * @param sList            sList
     * @param biConsumer       biConsumer
     * @param ignoreProperties ignoreProperties
     * @param <S>              S
     * @param <T>              T
     * @return LIST T
     */
    public static <S, T> List<T> convertList(Class<T> tClass, List<S> sList, BiConsumer<S, T> biConsumer, String... ignoreProperties) {
        if (sList == null || sList.isEmpty()) {
            return Collections.emptyList();
        }

        // 返回的list列表
        List<T> resultList;
        if (sList instanceof PageList) {
            PageList sourcePage = (PageList) sList;
            resultList = new PageList<>(sourcePage.getPageInfo(), sourcePage.size());
        } else {
            resultList = new ArrayList<>(sList.size());
        }

        // 循环调用转换单个对象
        for (S source : sList) {
            T target = ClassUtils.newInstance(tClass);
            resultList.add(convert(source, target, ignoreProperties));
            if (biConsumer != null) {
                biConsumer.accept(source, target);
            }
        }

        return resultList;
    }


    /**
     * 返回JavaBean所有属性的<code>PropertyDescriptor</code>
     *
     * @param beanClass the other class
     * @return the property descriptor []
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) {
        return SPRING_PRESENT ? org.springframework.beans.BeanUtils.getPropertyDescriptors(beanClass)
                : IntrospectionCache.forClass(beanClass).getPropertyDescriptors();
    }

    /**
     * 返回JavaBean给定JavaBean给定属性的 <code>PropertyDescriptors</code>
     *
     * @param beanClass the other class
     * @param property  the name of the property
     * @return the corresponding PropertyDescriptor, or <code>null</code> if none
     */
    public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String property) {
        return SPRING_PRESENT ? org.springframework.beans.BeanUtils.getPropertyDescriptor(beanClass, property)
                : IntrospectionCache.forClass(beanClass).getPropertyDescriptor(property);
    }


}
