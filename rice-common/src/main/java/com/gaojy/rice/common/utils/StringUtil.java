package com.gaojy.rice.common.utils;

/**
 * @author gaojy
 * @ClassName StringUtil.java
 * @Description TODO
 * @createTime 2022/01/03 00:23:00
 */
public class StringUtil {
    public static final String EMPTY = "";

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String join(String[] array) {
        if (ArrayUtils.isEmpty(array)) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
