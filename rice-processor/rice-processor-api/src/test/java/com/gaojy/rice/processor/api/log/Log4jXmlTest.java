package com.gaojy.rice.processor.api.log;

import org.apache.log4j.xml.DOMConfigurator;

/**
 * @author gaojy
 * @ClassName Log4jXmlTest.java
 * @Description
 * @createTime 2022/07/30 20:44:00
 */
public class Log4jXmlTest extends Log4jTest{

    @Override
    public void init() {
        DOMConfigurator.configure("src/test/resources/log4j-example.xml");
    }

    @Override
    public String getType() {
        return "xml";
    }
}
