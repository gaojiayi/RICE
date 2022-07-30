package com.gaojy.rice.processor.api.config;

import java.io.IOException;
import java.util.List;

/**
 * @author gaojy
 * @ClassName PackageScanner.java
 * @Description 
 * @createTime 2022/01/06 23:42:00
 */
public interface PackageScanner {
    public List<String> getFullyQualifiedClassNameList() throws IOException;
}
