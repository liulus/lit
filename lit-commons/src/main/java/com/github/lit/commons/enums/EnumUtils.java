package com.github.lit.commons.enums;

/**
 * User : liulu
 * Date : 2018-03-11 15:12
 * version $Id: EnumUtils.java, v 0.1 Exp $
 */
public class EnumUtils {


    public static <T extends IEnum> T get(Class<T> enumClass, String code){
        if (!enumClass.isEnum()) {
            return null;
        }
        for (T t : enumClass.getEnumConstants()) {
            if (t.getCode() != null && t.getCode().endsWith(code)) {
                return t;
            }
        }
        return null;
    }



}
