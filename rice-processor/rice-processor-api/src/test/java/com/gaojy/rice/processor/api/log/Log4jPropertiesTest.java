package com.gaojy.rice.processor.api.log;

import org.apache.log4j.PropertyConfigurator;

public class Log4jPropertiesTest extends Log4jTest {
    @Override
    public void init() {
        PropertyConfigurator.configure("src/test/resources/log4j-example.properties");
    }

    @Override
    public String getType() {
        return "properties";
    }
}
