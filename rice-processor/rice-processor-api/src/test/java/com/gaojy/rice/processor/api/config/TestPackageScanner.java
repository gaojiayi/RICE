package com.gaojy.rice.processor.api.config;

import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author gaojy
 * @ClassName TestPackageScanner.java
 * @Description 
 * @createTime 2022/01/26 09:49:00
 */
@RunWith(JUnit4.class)
public class TestPackageScanner {

    @Test
    public void scanTest() throws IOException {
        PackageScanner scan = new ClasspathPackageScanner("com.gaojy.rice.processor.api");
        System.out.println(scan.getFullyQualifiedClassNameList());

    }

}
