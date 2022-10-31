package com.gaojy.rice.demo.controller;

import com.gaojy.rice.controller.RiceControllerBootStrap;
import java.io.File;

/**
 * @author gaojy
 * @ClassName ControllerServer2.java
 * @Description
 * @createTime 2022/10/31 16:13:00
 */
public class ControllerServer3 {
    public static void main(String[] args) {
        String riceHome = System.getProperty("user.dir") +
            File.separator + "rice-demo" +
            File.separator + "src" +
            File.separator + "main" +
            File.separator + "resources";
        System.out.println(riceHome);
        System.setProperty("rice.controller.home.dir", riceHome);
        args = new String[] {
            "-cp", "9700",
            "-l", "127.0.0.1",
            "-d", "/Users/gaojiayi/logs/rice/controller/data/server3",
            "-m", "8700"
        };
        RiceControllerBootStrap.main0(args);
    }
}
