package com.gaojy.rice.controller;

import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author gaojy
 * @ClassName TestBootStrap.java
 * @Description
 * @createTime 2022/08/09 22:07:00
 */
@RunWith(JUnit4.class)
public class TestBootStrap {

    @Test
    public void testMain() {
        String riceHome = System.getProperty("user.dir") +
            File.separator + "src" +
            File.separator + "test" +
            File.separator + "resources";
        System.out.println(riceHome);
        System.setProperty("rice.controller.home.dir", riceHome);
        String[] args = {"-a", "127.0.0.1:8900,127.0.0.1:8901,127.0.0.1:8902", "-l", "127.0.0.1"};
        RiceControllerBootStrap.main0(args);
    }
}
