package com.gaojy.rice.common.extension.ext.impl;

import com.gaojy.rice.common.extension.ext.SimpleExt;
import java.net.URL;

/**
 * @author gaojy
 * @ClassName SimpleExtImpl1.java
 * @Description TODO
 * @createTime 2022/01/17 21:28:00
 */
public class SimpleExtImpl1 implements SimpleExt {
    public String echo(URL url, String s) {
        return "Ext1Impl1-echo";
    }

    public String yell(URL url, String s) {
        return "Ext1Impl1-yell";
    }

    public String bang(URL url, int i) {
        return "bang1";
    }
}
