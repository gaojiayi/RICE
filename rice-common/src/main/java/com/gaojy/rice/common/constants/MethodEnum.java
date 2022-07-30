package com.gaojy.rice.common.constants;

/**
 * @author gaojy
 * @ClassName MethodName.java
 * @Description
 * @createTime 2022/07/30 19:21:00
 */
public enum MethodEnum {
    PROCESS,
    MAP,
    REDUCE;

    public String getName() {
        return this.name().toLowerCase();
    }
}


