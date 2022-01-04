
package com.gaojy.rice.processor.log;

import com.gaojy.rice.common.constants.LoggerName;
import java.lang.reflect.Method;
import java.net.URL;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RiceClientLogger {
    public static final String CLIENT_LOG_ROOT = "rice.client.logRoot";
    public static final String CLIENT_LOG_MAXINDEX = "rice.client.logFileMaxIndex";
    public static final String CLIENT_LOG_LEVEL = "rice.client.logLevel";

    private static Logger log;

    private static Class logClass = null;

    private static Logger createLogger(final String loggerName) {
        // 如果定义了rocketmq日志配置文件的路径的话  就优先使用，否则使用默认的
        String logConfigFilePath =
            System.getProperty("rice.client.log.configFile",
                System.getenv("RICE_CLIENT_LOG_CONFIGFILE"));
        Boolean isloadconfig =
            Boolean.parseBoolean(System.getProperty("rice.client.log.loadconfig", "true"));

        // 下面3个是资源文件地址
        final String log4JResourceFile =
            System.getProperty("rice.client.log4j.resource.fileName", "log4j_rice_client.xml");

        final String logbackResourceFile =
            System.getProperty("rice.client.logback.resource.fileName", "logback_rice_client.xml");

        final String log4J2ResourceFile =
            System.getProperty("rice.client.log4j2.resource.fileName", "log4j2_rice_client.xml");

        // 定义日志写文件地址
        String clientLogRoot = System.getProperty(CLIENT_LOG_ROOT, "${user.home}/logs/rice-logs");
        System.setProperty("client.logRoot", clientLogRoot);
        // 定义日志级别
        String clientLogLevel = System.getProperty(CLIENT_LOG_LEVEL, "INFO");
        System.setProperty("client.logLevel", clientLogLevel);
        // 定义日志数
        String clientLogMaxIndex = System.getProperty(CLIENT_LOG_MAXINDEX, "10");
        System.setProperty("client.logFileMaxIndex", clientLogMaxIndex);

        if (isloadconfig) {
            try {
                // 确定具体使用哪一个日志框架
                ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
                Class classType = iLoggerFactory.getClass();
                if (classType.getName().equals("org.slf4j.impl.Log4jLoggerFactory")) {
                    Class<?> domconfigurator;
                    Object domconfiguratorobj;
                    domconfigurator = Class.forName("org.apache.log4j.xml.DOMConfigurator");
                    domconfiguratorobj = domconfigurator.newInstance();
                    if (null == logConfigFilePath) {
                        Method configure = domconfiguratorobj.getClass().getMethod("configure", URL.class);
                        URL url = RiceClientLogger.class.getClassLoader().getResource(log4JResourceFile);
                        configure.invoke(domconfiguratorobj, url);
                    } else {
                        Method configure = domconfiguratorobj.getClass().getMethod("configure", String.class);
                        configure.invoke(domconfiguratorobj, logConfigFilePath);
                    }

                } else if (classType.getName().equals("ch.qos.logback.classic.LoggerContext")) {
                    Class<?> joranConfigurator;
                    Class<?> context = Class.forName("ch.qos.logback.core.Context");
                    Object joranConfiguratoroObj;
                    joranConfigurator = Class.forName("ch.qos.logback.classic.joran.JoranConfigurator");
                    joranConfiguratoroObj = joranConfigurator.newInstance();
                    Method setContext = joranConfiguratoroObj.getClass().getMethod("setContext", context);
                    setContext.invoke(joranConfiguratoroObj, iLoggerFactory);
                    if (null == logConfigFilePath) {
                        URL url = RiceClientLogger.class.getClassLoader().getResource(logbackResourceFile);
                        Method doConfigure =
                            joranConfiguratoroObj.getClass().getMethod("doConfigure", URL.class);
                        doConfigure.invoke(joranConfiguratoroObj, url);
                    } else {
                        Method doConfigure =
                            joranConfiguratoroObj.getClass().getMethod("doConfigure", String.class);
                        doConfigure.invoke(joranConfiguratoroObj, logConfigFilePath);
                    }

                } else if (classType.getName().equals("org.apache.logging.slf4j.Log4jLoggerFactory")) {
                    Class<?> joranConfigurator = Class.forName("org.apache.logging.log4j.core.config.Configurator");
                    Method initialize = joranConfigurator.getDeclaredMethod("initialize", String.class, String.class);
                    if (null == logConfigFilePath) {
                        initialize.invoke(joranConfigurator, "log4j2", log4J2ResourceFile);
                    } else {
                        initialize.invoke(joranConfigurator, "log4j2", logConfigFilePath);
                    }
                }
                logClass = classType;
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        return LoggerFactory.getLogger(LoggerName.CLIENT_LOGGER_NAME);
    }

    public static Logger getLog() {
        if (log == null) {
            synchronized (RiceClientLogger.class) {
                if (log == null) {
                    log = createLogger(LoggerName.CLIENT_LOGGER_NAME);
                    return log;
                }
            }
        }
        return log;

    }

    public static void setLog(Logger log) {
        RiceClientLogger.log = log;
    }



    public static void main(String[] args) {
        /**
         * 在${user.home}/logs/rocketmqlogs中找到了日志文件
         */
        RiceClientLogger.getLog().info("+++++");
    }
}

