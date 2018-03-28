package com.github.lit.code.util;

import java.util.Map;

/**
 * User : liulu
 * Date : 2018/2/7 15:36
 * version $Id: ConstantUtils.java, v 0.1 Exp $
 */
public class TokenUtils {

    private static final String OPEN_TOKEN = "${";

    private static final String CLOSE_TOKEN = "}";


    public static String parseToken(Map<String, String> context, String text) {
        return parseToken(context, text, OPEN_TOKEN, CLOSE_TOKEN);
    }

    public static String parseToken(Map<String, String> context, String text, String openToken, String closeToken) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        StringBuilder result = new StringBuilder(text.length() + 100);

        char[] src = text.toCharArray();
        int offset = 0;
        int start = text.indexOf(openToken, offset);

        while (start > -1) {
            result.append(src, offset, start - offset);
            // 查找 closeToken
            int end = text.indexOf(closeToken, start);
            // closeToken 不存在
            if (end < 0) {
                offset = start;
                break;
            }
            // openToken 和 closeToken 之间的 key
            String key = String.valueOf(src, start + openToken.length(), end - start - openToken.length());
            // key 对应的 value 存在 -> 替换, 不存在 -> 不做处理
            String value = context.get(key);
            if (value != null && !value.isEmpty()) {
                result.append(value);
            } else {
                result.append(src, start, end - start + closeToken.length());
            }
            offset = end + closeToken.length();
            start = text.indexOf(openToken, offset);
        }

        if (src.length - offset > 0) {
            result.append(src, offset, src.length - offset);
        }
        return result.toString();
    }


}
