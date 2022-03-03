package com.gaojy.rice.common.constants;

import java.util.Locale;

/**
 * @author gaojy
 * @ClassName ExecuteType.java
 * @Description 执行类型
 * @createTime 2022/01/04 19:16:00
 */
@Deprecated
public enum ExecuteType {
    /**
     * 广播执行
     */
    BROADCAST,

    /**
     * 单机执行
     */
    STANDALONE;

    public static ExecuteType getType(String type){
        return ExecuteType.valueOf(type.toUpperCase());
    }
}
