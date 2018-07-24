package com.github.lit.support.mybatis.builder;

import com.github.lit.util.NameUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author liulu
 * @version : v1.0
 * date : 2018/7/15 20:35
 */
public abstract class SerializedLambdaUtils {

    public static  String getProperty(PropertyFunction propertyFunction) {
        String getMethod = getSerializedLambda(propertyFunction).getImplMethodName();

        if (getMethod.startsWith("get")) {
            return NameUtils.getFirstLowerName(getMethod.substring(3));
        }
        if (getMethod.startsWith("is")) {
            return NameUtils.getFirstLowerName(getMethod.substring(2));
        }
        return NameUtils.getFirstLowerName(getMethod);
    }

    public static SerializedLambda getSerializedLambda(PropertyFunction propertyFunction) {
        try {
            Method writeReplace = propertyFunction.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(Boolean.TRUE);
            return (SerializedLambda) writeReplace.invoke(propertyFunction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
