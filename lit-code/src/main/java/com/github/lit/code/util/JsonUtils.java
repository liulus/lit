package com.github.lit.code.util;

import com.oracle.javafx.jmx.json.JSONDocument;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * User : liulu
 * Date : 2018/2/6 16:58
 * version $Id: JsonUtils.java, v 0.1 Exp $
 */
public class JsonUtils {

    public static <T> T toObject(JSONDocument jsonDocument, Class<T> clazz) {

        T result = ClassUtils.newInstance(clazz);

        for (Field field : clazz.getDeclaredFields()) {
            Type type = field.getGenericType();
            if (type.equals(byte.class) || type.equals(Byte.class)
                    || type.equals(short.class) || type.equals(Short.class)
                    || type.equals(int.class) || type.equals(Integer.class)
                    || type.equals(long.class) || type.equals(Long.class)
                    || type.equals(float.class) || type.equals(Float.class)
                    || type.equals(double.class) || type.equals(Double.class)
                    || type.equals(BigDecimal.class) || type.equals(Number.class)) {

                Number value = jsonDocument.getNumber(field.getName());
                ClassUtils.setFieldValue(field, result, value);
            } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                Boolean value = jsonDocument.getBoolean(field.getName());
                ClassUtils.setFieldValue(field, result, value);
            } else if (type.equals(String.class)) {
                String value = jsonDocument.getString(field.getName());
                ClassUtils.setFieldValue(field, result, value);
            }
        }

        return result;
    }


}
