package com.github.lit.code.util;

import com.oracle.javafx.jmx.json.JSONDocument;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User : liulu
 * Date : 2018/2/7 16:35
 * version $Id: BeanUtils.java, v 0.1 Exp $
 */
public class BeanUtils {



    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {

        T result = ClassUtils.newInstance(beanClass);

        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            Object value = map.get(field.getName());

            if (value instanceof JSONDocument) {
                JSONDocument jsonDocument = (JSONDocument) value;
                if (jsonDocument.isArray()) {
                    List valueList = new ArrayList<>();
                    for (Object o : jsonDocument.array()) {
                        if (o instanceof JSONDocument) {
                            break;
                        }
                        valueList.add(o);
                    }
                    value = valueList;
                }
                if (jsonDocument.isObject()) {
                    value = mapToBean(jsonDocument.object(), field.getType());
                }
            }
            if (value != null) {
                ClassUtils.setFieldValue(field, result, value);
            }

        }
        return result;
    }





}
