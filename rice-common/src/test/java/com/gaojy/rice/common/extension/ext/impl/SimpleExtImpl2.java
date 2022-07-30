package com.gaojy.rice.common.extension.ext.impl;

import com.gaojy.rice.common.extension.ext.SimpleExt;
import java.net.URL;

/**
 * @author gaojy
 * @ClassName SimpleExtImpl1.java
 * @Description 
 * @createTime 2022/01/17 21:28:00
 */
public class SimpleExtImpl2 implements SimpleExt {
    public String echo(URL url, String s) {
        return "Ext1Impl2-echo";
    }

    public String yell(URL url, String s) {
        return "Ext1Impl2-yell";
    }

    public String bang(URL url, int i) {
        return "bang2";
    }
}
