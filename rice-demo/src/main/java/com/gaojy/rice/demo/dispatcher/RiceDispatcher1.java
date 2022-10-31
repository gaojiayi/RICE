package com.gaojy.rice.demo.dispatcher;

import com.gaojy.rice.controller.RiceControllerBootStrap;
import com.gaojy.rice.dispatcher.RiceDispatchBootStrap;
import java.io.File;

/**
 * @author gaojy
 * @ClassName RiceDispatcher1.java
 * @Description
 * @createTime 2022/10/31 16:51:00
 */
public class RiceDispatcher1 {
    public static void main(String[] args) {
        String riceHome = System.getProperty("user.dir") +
            File.separator + "rice-demo" +
            File.separator + "src" +
            File.separator + "main" +
            File.separator + "resources";
        System.out.println(riceHome);
        System.setProperty("rice.dispatcher.home.dir", riceHome);
        RiceDispatchBootStrap.main0(args);
    }
}
