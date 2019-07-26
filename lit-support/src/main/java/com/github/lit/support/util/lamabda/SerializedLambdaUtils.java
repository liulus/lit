package com.github.lit.support.util.lamabda;

import com.github.lit.support.util.ClassUtils;
import com.github.lit.support.util.NameUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author liulu
 * @version : v1.0
 * date : 2018/7/15 20:35
 */
public abstract class SerializedLambdaUtils {

    private SerializedLambdaUtils() {
    }

    public static String getProperty(SerializedFunction propertyFunction) {
        String getMethod = getSerializedLambda(propertyFunction).getImplMethodName();

        if (getMethod.startsWith("get")) {
            return NameUtils.getFirstLowerName(getMethod.substring(3));
        }
        if (getMethod.startsWith("is")) {
            return NameUtils.getFirstLowerName(getMethod.substring(2));
        }
        return NameUtils.getFirstLowerName(getMethod);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> Class<T> getLambdaClass(SerializedFunction<T, R> propertyFunction) {
        SerializedLambda serializedLambda = getSerializedLambda(propertyFunction);
        String className = ClassUtils.convertResourcePathToClassName(serializedLambda.getImplClass());
        return (Class<T>) ClassUtils.forName(className);
    }

    public static SerializedLambda getSerializedLambda(SerializedFunction propertyFunction) {
        try {
            Method writeReplace = propertyFunction.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(Boolean.TRUE);
            return (SerializedLambda) writeReplace.invoke(propertyFunction);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
