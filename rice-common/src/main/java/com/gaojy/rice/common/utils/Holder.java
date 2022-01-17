package com.gaojy.rice.common.utils;

/**
 * @author gaojy
 * @ClassName Holder.java
 * @Description TODO
 * @createTime 2022/01/17 12:06:00
 */
public class Holder<T> {

    private volatile T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

}
