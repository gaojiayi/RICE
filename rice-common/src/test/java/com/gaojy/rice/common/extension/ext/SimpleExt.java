package com.gaojy.rice.common.extension.ext;

import com.gaojy.rice.common.extension.SPI;
import java.net.URL;

/**
 * @author gaojy
 * @ClassName SimpleExt.java
 * @Description TODO
 * @createTime 2022/01/17 21:27:00
 */
@SPI
public interface SimpleExt {
    String echo(URL url, String s);

    String yell(URL url, String s);

    // no @Adaptive
    String bang(URL url, int i);
}
