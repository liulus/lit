package com.github.lit.commons.bean;

import com.github.lit.commons.page.PageList;
import com.github.lit.commons.util.ClassUtils;
import com.github.lit.commons.util.NameUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 本类来源于 dexcoder-commons
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
        return mapToBean(map, beanClass, '_');
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
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass, Character delimiter) {

        T bean = ClassUtils.newInstance(beanClass);
        if (map == null || map.isEmpty()) {
            return bean;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {

            Object value = entry.getValue();
            if (value != null) {
                String propertyName = entry.getKey();
                if (delimiter != null) {
                    propertyName = propertyName.contains(delimiter.toString()) ?
                            NameUtils.getCamelName(propertyName, delimiter)
                            : propertyName.toLowerCase();
                }
                invokeWriteMethod(bean, propertyName, value);
            }
        }
        return bean;
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
        return mapToBean(mapList, beanClass, '_');
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
    public static <T> List<T> mapToBean(List<Map<String, Object>> mapList, Class<T> beanClass, Character delimiter) {

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
     * 单个对象转换
     *
     * @param target 目标对象
     * @param source 源对象
     * @param <T>    目标对象类型
     * @param <S>    源对象类型
     * @return 转换后的目标对象
     */
    public static <T, S> T convert(T target, S source) {
        return convert(target, source, null, null);
    }

    /**
     * 单个对象转换
     *
     * @param target           目标对象
     * @param source           源对象
     * @param <T>              目标对象类型
     * @param <S>              源对象类型
     * @param ignoreProperties 需要过滤的属性
     * @return 转换后的目标对象
     */
    public static <T, S> T convert(T target, S source, String[] ignoreProperties) {
        return convert(target, source, ignoreProperties, null);
    }

    /**
     * 单个对象转换
     *
     * @param target           目标对象
     * @param source           源对象
     * @param ignoreProperties 需要过滤的属性
     * @param callBack         简单属性拷贝完成后的回调
     * @param <T>              目标对象类型
     * @param <S>              源对象类型
     * @return
     */
    private static <T, S> T convert(T target, S source, String[] ignoreProperties, ConvertCallBack<T, S> callBack) {

        if (target == null || source == null) {
            return null;
        }

        //过滤的属性
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

        //拷贝相同的属性
        copySameProperties(target, source, ignoreList);
        if (callBack != null) {
            callBack.convertCallBack(target, source);
        }

        return target;
    }


    /**
     * 拷贝相同的属性
     *
     * @param target     the target
     * @param source     the source
     * @param ignoreList the ignore list
     * @param <T>        目标对象类型
     * @param <S>        源对象类型
     */
    private static <T, S> void copySameProperties(T target, S source, List<String> ignoreList) {

        //获取目标对象属性信息
        PropertyDescriptor[] targetPds = getPropertyDescriptors(target.getClass());

        for (PropertyDescriptor targetPd : targetPds) {
            if (targetPd.getWriteMethod() == null || (ignoreList != null && ignoreList.contains(targetPd.getName()))) {
                continue;
            }
            Object value = invokeReaderMethod(source, targetPd.getName());
            if (value != null && (targetPd.getReadMethod() == null || ClassUtils.invokeMethod(targetPd.getReadMethod(), target) == null)) {
                ClassUtils.invokeMethod(targetPd.getWriteMethod(), target, value);
            }
        }
    }

    /**
     * 列表转换
     *
     * @param clazz the clazz
     * @param list  the list
     * @param <T>   目标对象类型
     * @param <S>   源对象类型
     * @return the page list
     */
    public static <T, S> List<T> convert(Class<T> clazz, List<S> list) {
        return convert(clazz, list, null, (ConvertCallBack<T, S>) null);
    }

    public static <T, S> List<T> convert(Class<T> clazz, List<S> list, ConvertCallBack<T, S> callBack) {
        return convert(clazz, list, null, callBack);
    }

    /**
     * 列表转换
     *
     * @param targetClz        the clazz
     * @param sourceList       the list
     * @param ignoreProperties the ignore properties
     * @param <T>              目标对象类型
     * @param <S>              源对象类型
     * @return the page list
     */
    public static <T, S> List<T> convert(Class<T> targetClz, List<S> sourceList, String[] ignoreProperties) {
        return convert(targetClz, sourceList, ignoreProperties, (ConvertCallBack<T, S>) null);
    }

    public static <T, S> List<T> convert(Class<T> targetClz, List<S> sourceList, String[] ignoreProperties, ConvertCallBack<T, S> callBack) {

        //返回的list列表
        List<T> resultList;

        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        } else if (sourceList instanceof PageList) {
            PageList sourcePage = (PageList) sourceList;
            resultList = new PageList<>(sourcePage.getPageInfo(), sourcePage.size());
        } else {
            resultList = new ArrayList<>();
        }

        //循环调用转换单个对象
        for (S sources : sourceList) {
            T target = ClassUtils.newInstance(targetClz);
            resultList.add(convert(target, sources, ignoreProperties, callBack));
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
     * @param propertyName the name of the property
     * @return the corresponding PropertyDescriptor, or <code>null</code> if none
     */
    public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName) {
        return IntrospectionCache.forClass(beanClass).getPropertyDescriptor(propertyName);
    }

    /**
     * 执行指定属性的 reader 方法 （get方法）
     *
     * @param bean         执行方法的对象
     * @param propertyName 执行reader 方法的属性
     * @return 方法返回值
     */
    public static Object invokeReaderMethod(Object bean, String propertyName) {
        if (bean == null) {
            return null;
        }
        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), propertyName);
        return pd == null ? null : ClassUtils.invokeMethod(pd.getReadMethod(), bean);
    }

    /**
     * 执行指定属性的 Write 方法 （set方法）
     *
     * @param bean         执行方法的对象
     * @param propertyName 执行Write 方法的属性
     * @param values       要设置的值
     */
    public static void invokeWriteMethod(Object bean, String propertyName, Object... values) {
        if (bean == null) {
            return;
        }
        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), propertyName);
        Method writeMethod;
        if (pd == null || (writeMethod = pd.getWriteMethod()) == null) {
            return;
        }
        ClassUtils.invokeMethod(writeMethod, bean, values);
    }

}
