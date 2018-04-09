package com.github.lit.commons.bean;

import com.github.lit.commons.page.PageList;
import com.github.lit.commons.util.ClassUtils;
import com.github.lit.commons.util.NameUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Java Bean 对象转换器
 * User : liulu
 * Date : 2016-10-6 9:29
 */
public class BeanUtils {

    /**
     * map转为bean, key名为bean属性名
     *
     * @param map       the map
     * @param beanClass the other class
     * @param <T>       bean 的类型
     * @return t
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {


        return mapToBean(map, beanClass, null);
    }

    /**
     * map转为bean，key名为下划线命名方式
     *
     * @param map       the map
     * @param beanClass the other class
     * @param <T>       bean 的类型
     * @return t
     */
    public static <T> T underLineKeyMapToBean(Map<String, Object> map, Class<T> beanClass) {
        return mapToBean(map, beanClass, "_");
    }

    /**
     * map转为bean，最后一个参数指定map中的key转换成骆驼命名法(JavaBean中惯用的属性命名)的分隔符,例如login_name转换成loginName,分隔符为下划线_
     * 指定了分隔符进行转换时如果属性不带分隔符会统一转成小写,为空则不进行任何转换
     *
     * @param map       the map
     * @param beanClass the other class
     * @param delimiter the delimiter
     * @param <T>       bean 的类型
     * @return t
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass, String delimiter) {

        if (map == null || map.isEmpty()) {
            return null;
        }

        T result = ClassUtils.newInstance(beanClass);
        for (Map.Entry<String, Object> entry : map.entrySet()) {

            Object value = entry.getValue();
            if (value != null) {
                String property = entry.getKey();
                if (delimiter != null) {
                    property = property.contains(delimiter) ?
                            NameUtils.getCamelName(property, delimiter)
                            : property.toLowerCase();
                }
                invokeWriteMethod(result, property, value);
            }
        }
        return result;
    }

    /**
     * map转为bean，key名为bean属性名
     *
     * @param <T>       the type parameter
     * @param mapList   the map list
     * @param beanClass the other class
     * @param <T>       bean 的类型
     * @return t list
     */
    public static <T> List<T> mapToBean(List<Map<String, Object>> mapList, Class<T> beanClass) {
        return mapToBean(mapList, beanClass, null);
    }

    /**
     * map转为bean，key名为下划线命名方式
     *
     * @param <T>       the type parameter
     * @param mapList   the map list
     * @param beanClass the other class
     * @param <T>       bean 的类型
     * @return t list
     */
    public static <T> List<T> underlineKeyMapToBean(List<Map<String, Object>> mapList, Class<T> beanClass) {
        return mapToBean(mapList, beanClass, "_");
    }

    /**
     * map转为bean，最后一个参数指定map中的key转换成骆驼命名法(JavaBean中惯用的属性命名)的分隔符,例如login_name转换成loginName,分隔符为下划线_
     * 指定了分隔符进行转换时如果属性不带分隔符会统一转成小写,为空则不进行任何转换
     *
     * @param mapList   the map list
     * @param beanClass the other class
     * @param delimiter the delimiter
     * @param <T>       the type parameter
     * @return t list
     */
    public static <T> List<T> mapToBean(List<Map<String, Object>> mapList, Class<T> beanClass, String delimiter) {

        if (mapList == null) {
            return null;
        }
        List<T> beanList = new ArrayList<>(mapList.size());
        for (Map<String, Object> map : mapList) {
            T t = mapToBean(map, beanClass, delimiter);
            beanList.add(t);
        }
        return beanList;
    }

    /**
     * single convert
     *
     * @param source the source
     * @param target the target
     * @param <S>    source type
     * @param <T>    target type
     * @return target
     */
    public static <S, T> T convert(S source, T target) {
        return convert(source, target, (String[]) null);
    }

    /**
     * single convert
     *
     * @param source           the source
     * @param target           the target
     * @param ignoreProperties the ignore list
     * @param <S>              source type
     * @param <T>              target type
     * @return target
     */
    public static <S, T> T convert(S source, T target, String... ignoreProperties) {
        if (target == null || source == null) {
            return target;
        }

        List<String> ignoreList = ignoreProperties == null ? null : Arrays.asList(ignoreProperties);
        copySameProperties(source, target, ignoreList);
        return target;
    }


    /**
     * 拷贝相同的属性, 若目标属性值不为空, 则忽略
     *
     * @param target     the target
     * @param source     the source
     * @param ignoreList the ignore list
     * @param <S>        source type
     * @param <T>        target type
     */
    private static <S, T> void copySameProperties(S source, T target, List<String> ignoreList) {

        //获取目标对象属性信息
        PropertyDescriptor[] targetPds = getPropertyDescriptors(target.getClass());

        for (PropertyDescriptor targetPd : targetPds) {
            if (targetPd.getWriteMethod() == null || (ignoreList != null && ignoreList.contains(targetPd.getName()))) {
                continue;
            }

            Object value = invokeReaderMethod(source, targetPd.getName());
            if (value != null) {
                ClassUtils.invokeMethod(targetPd.getWriteMethod(), target, value);
            }
        }
    }

    /**
     * list convert
     *
     * @param targetClass the target clazz
     * @param sourceList  the source list
     * @param <S>         source type
     * @param <T>         target type
     * @return target list
     */
    public static <S, T> List<T> convert(Class<T> targetClass, List<S> sourceList) {
        return convert(targetClass, sourceList, null, (String[]) null);
    }

    /**
     * list convert
     *
     * @param targetClass the target clazz
     * @param sourceList  the source list
     * @param callBack    after convert callback
     * @param <S>         source type
     * @param <T>         target type
     * @return target list
     */
    public static <S, T> List<T> convert(Class<T> targetClass, List<S> sourceList, ConvertCallBack<S, T> callBack) {
        return convert(targetClass, sourceList, callBack, (String[]) null);
    }

    /**
     * list convert
     *
     * @param targetClass      the target clazz
     * @param sourceList       the source list
     * @param ignoreProperties the ignore properties
     * @param <S>              source type
     * @param <T>              target type
     * @return target list
     */
    public static <S, T> List<T> convert(Class<T> targetClass, List<S> sourceList, String... ignoreProperties) {
        return convert(targetClass, sourceList, null, ignoreProperties);
    }

    /**
     * list convert
     *
     * @param targetClass      the target clazz
     * @param sourceList       the source list
     * @param callBack         after convert callback
     * @param ignoreProperties the ignore properties
     * @param <S>              source type
     * @param <T>              target type
     * @return target list
     */
    public static <S, T> List<T> convert(Class<T> targetClass, List<S> sourceList, ConvertCallBack<S, T> callBack, String... ignoreProperties) {

        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }

        // 返回的list列表
        List<T> resultList;
        if (sourceList instanceof PageList) {
            PageList sourcePage = (PageList) sourceList;
            resultList = new PageList<>(sourcePage.getPageInfo(), sourcePage.size());
        } else {
            resultList = new ArrayList<>(sourceList.size());
        }

        // 循环调用转换单个对象
        for (S source : sourceList) {
            T target = ClassUtils.newInstance(targetClass);
            resultList.add(convert(source, target, ignoreProperties));
            if (callBack != null) {
                callBack.convertCallBack(source, target);
            }
        }

        return resultList;
    }

    /**
     * 返回JavaBean所有属性的<code>PropertyDescriptor</code>
     *
     * @param beanClass the other class
     * @return the property descriptor [ ]
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> beanClass) {
        return IntrospectionCache.forClass(beanClass).getPropertyDescriptors();
    }

    /**
     * 返回JavaBean给定JavaBean给定属性的 <code>PropertyDescriptors</code>
     *
     * @param beanClass    the other class
     * @param property the name of the property
     * @return the corresponding PropertyDescriptor, or <code>null</code> if none
     */
    public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String property) {
        return IntrospectionCache.forClass(beanClass).getPropertyDescriptor(property);
    }

    /**
     * 执行指定属性的 reader 方法 （get方法）
     *
     * @param bean         执行方法的对象
     * @param property 执行reader 方法的属性
     * @return 方法返回值
     */
    public static Object invokeReaderMethod(Object bean, String property) {
        if (bean == null) {
            return null;
        }
        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), property);
        return pd == null ? null : ClassUtils.invokeMethod(pd.getReadMethod(), bean);
    }

    /**
     * 执行指定属性的 Write 方法 （set方法）
     *
     * @param bean         执行方法的对象
     * @param property 执行Write 方法的属性
     * @param values       要设置的值
     */
    public static void invokeWriteMethod(Object bean, String property, Object... values) {
        if (bean == null) {
            return;
        }
        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), property);
        Method writeMethod;
        if (pd == null || (writeMethod = pd.getWriteMethod()) == null) {
            return;
        }
        ClassUtils.invokeMethod(writeMethod, bean, values);
    }

}
