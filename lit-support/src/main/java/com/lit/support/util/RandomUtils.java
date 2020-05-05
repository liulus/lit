package com.lit.support.util;

import java.util.Random;

/**
 * User : liulu
 * Date : 2018/3/15 16:56
 * version $Id: RandomUtils.java, v 0.1 Exp $
 */
public abstract class RandomUtils {

    private RandomUtils() {}

    private static final char[] LOWER_CASE = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    private static final char[] UPPER_CASE = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static final int[] NUMBER = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    private static final Random RANDOM = new Random();


    public static String getNumber(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append((RANDOM.nextInt(10)));
        }
        return result.toString();
    }

    public static String getLowCase(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(LOWER_CASE.length);
            result.append(LOWER_CASE[randomIndex]);
        }
        return result.toString();
    }

    public static String getUpperCase(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(UPPER_CASE.length);
            result.append(UPPER_CASE[randomIndex]);
        }
        return result.toString();
    }


}
