package com.lit.support.util;

/**
 * 命名工具类
 * User : liulu
 * Date : 2016-10-4 16:05
 */
public abstract class NameUtils {

    /**
     * 下划线分割命名转换为驼峰命名
     *
     * @param name 下划线命名
     * @return 驼峰命名
     */
    public static String getCamelName(String name) {
        return getCamelName(name, "_");
    }

    /**
     * 获取指定字符分隔的驼峰命名
     *
     * @param name      指定分隔符命名
     * @param delimiter 分隔符
     * @return 驼峰命名
     */
    public static String getCamelName(String name, String delimiter) {

        if (name == null || name.isEmpty()) {
            return "";
        }

        char delimiterChar = delimiter.charAt(0);

        StringBuilder sb = new StringBuilder();
        name = name.toLowerCase();

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == delimiterChar) {
                i++;
                sb.append(Character.toUpperCase(name.charAt(i)));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 将名称首字母大写
     *
     * @param name 原名称
     * @return 首字母大写名称
     */
    public static String getFirstUpperName(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * @param name 原名称
     * @return 首字母小写名称
     */
    public static String getFirstLowerName(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        if (Character.isLowerCase(name.charAt(0))) {
            return name;
        }
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * 驼峰命名转换为小写下划线分割命名
     *
     * @param name 驼峰命名
     * @return 下划线命名
     */
    public static String getUnderLineName(String name) {
        return getLowerDelimiterName(name, "_");
    }

    /**
     * 驼峰命名转换为小写指定分隔符命名
     *
     * @param name      驼峰命名
     * @param delimiter 指定分隔符
     * @return 小写指定分隔符命名
     */
    public static String getLowerDelimiterName(String name, String delimiter) {
        return getUpperDelimiterName(name, delimiter).toLowerCase();
    }

    /**
     * 驼峰命名转换为大写指定分隔符命名
     *
     * @param name      驼峰命名
     * @param delimiter 指定分隔符
     * @return 大写指定分隔符命名
     */
    public static String getUpperDelimiterName(String name, String delimiter) {

        if (name == null || name.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                sb.append(delimiter);
            }
            sb.append(c);
        }
        return sb.toString().toUpperCase();
    }

}
