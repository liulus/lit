package com.github.lit.commons.util;

/**
 * User : liulu
 * Date : 2018-03-11 16:00
 * version $Id: Assert.java, v 0.1 Exp $
 */
public abstract class Assert {

    public static void isNull(Object object, String message) {
        if(object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object, String message) {
        if(object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isEmpty(String text, String message) {
        if (text != null && text.length() > 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(String text, String message) {
        if (text == null || text.length() <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
