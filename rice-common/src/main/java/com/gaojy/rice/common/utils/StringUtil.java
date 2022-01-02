package com.gaojy.rice.common.utils;

/**
 * @author gaojy
 * @ClassName StringUtil.java
 * @Description TODO
 * @createTime 2022/01/03 00:23:00
 */
public class StringUtil {

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
}
