package com.gaojy.rice.controller;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.alipay.remoting.RemotingContext;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.utils.CommandUtil;
import com.gaojy.rice.common.utils.MixAll;
import com.gaojy.rice.common.utils.RiceBanner;
import com.gaojy.rice.common.utils.StringUtil;
import com.gaojy.rice.controller.config.ControllerConfig;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.TransfServerConfig;
import com.gaojy.rice.remote.transport.TransfSystemConfig;
import com.gaojy.rice.repository.api.Repository;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName RIceControllerBootStrap.java
 * @Description 控制器启动类
 * 1 添加System Property需要按照-Dname=value的形式指定，并且需要注意的是，
 * 这个参数需要放在Java和-jar命令的中间，否则是没有作用的，如Java -Dxx=xx -jar app.jar
 * 2 添加Program Argument参数时使用–，如Java -jar app.jar –xx.xx=xx,
 * 并且需要注意的是此类型参数需要放在Java -jar 命令参数的后面 。
 * 3 配置优先级 命令行 > 配置文件
 * @createTime 2022/01/08 10:39:00
 */
public class RiceControllerBootStrap {
    public static Properties properties = null;
    public static CommandLine commandLine = null;

    public static void main(String[] args) {
        main0(args);
    }

    public static RiceController main0(String[] args) {
        if (null == System.getProperty(TransfSystemConfig.COM_RICE_REMOTING_SOCKET_SNDBUF_SIZE)) {
            TransfSystemConfig.socketSndbufSize = 4096;
        }
        if (null == System.getProperty(TransfSystemConfig.COM_RICE_REMOTING_SOCKET_RCVBUF_SIZE)) {
            TransfSystemConfig.socketRcvbufSize = 4096;
        }

        Options options = CommandUtil.buildCommandlineOptions(new Options());
        commandLine = CommandUtil.parseCmdLine("rice-controller", args, buildCommandlineOptions(options), new PosixParser());
        if (null == commandLine) {
            System.exit(-1);
            return null;
        }
        ControllerConfig controllerConfig = new ControllerConfig();
        TransfServerConfig transfServerConfig = new TransfServerConfig();
        // 默认业务端口
        // controllerConfig.setControllerPort(9876);
        try {
            String file = controllerConfig.getRiceHome() + File.separator + "conf" + File.separator + "rice-controller.properties";
            //  优先级最低的配置是文件
            if (commandLine.hasOption('c')) {
                file = commandLine.getOptionValue('f');
            }
            if (file != null) {
                InputStream in = new BufferedInputStream(new FileInputStream(file));
                properties = new Properties();
                properties.load(in);
                MixAll.properties2Object(properties, controllerConfig);
                MixAll.properties2Object(properties, transfServerConfig);

                controllerConfig.setConfigStorePath(file);

                //repository related config
                System.setProperty(Repository.REPOSITORY_PASSWORD_KEY,
                    properties.getProperty(Repository.REPOSITORY_PASSWORD_KEY));
                System.setProperty(Repository.REPOSITORY_TYPE_KEY,
                    properties.getProperty(Repository.REPOSITORY_TYPE_KEY));
                System.setProperty(Repository.REPOSITORY_USERNAME_KEY,
                    properties.getProperty(Repository.REPOSITORY_USERNAME_KEY));
                System.setProperty(Repository.REPOSITORY_URL_KEY,
                    properties.getProperty(Repository.REPOSITORY_URL_KEY));

                System.out.printf("load config properties file OK, " + file + "%n");
                in.close();
            }

            if (commandLine.hasOption('p')) {
                MixAll.printObjectProperties(null, controllerConfig);
                MixAll.printObjectProperties(null, transfServerConfig);
                System.exit(0);
            }
            // 优先级其次是来自命令行的参数
            MixAll.properties2Object(CommandUtil.commandLine2Properties(commandLine), controllerConfig);

            if (null == controllerConfig.getRiceHome()) {
                System.out.printf("Please set the " + MixAll.RICE_HOME_ENV + " variable in your environment to match the location of the RocketMQ installation%n");
                System.exit(-2);
            }

            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            lc.reset();
            configurator.doConfigure(controllerConfig.getRiceHome() + File.separator + "conf"
                + File.separator + "logback_controller.xml");
            final Logger log = LoggerFactory.getLogger(LoggerName.CONTROLLER_LOGGER_NAME);

            MixAll.printObjectProperties(log, controllerConfig);
            MixAll.printObjectProperties(log, transfServerConfig);
            final RiceController controller = new RiceController(controllerConfig, transfServerConfig);
            // TODO: 后面再接入配置持久化方案
            try {
                controller.start();
                // 打印banner
                RiceBanner.show(7);
                String tip = "The RICE Controller boot success. serializeType=" + RiceRemoteContext.getSerializeTypeConfigInThisServer();
                log.info(tip);
                System.out.printf(tip + "%n");
            } catch (Exception e) {
                controller.shutdown();
                System.exit(-3);
            }
            // JVM 钩子
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                controller.shutdown();
            }));
            return controller;
        } catch (IOException | JoranException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public static Options buildCommandlineOptions(final Options options) {
        // 配置文件地址
        Option opt = new Option("f", "configFile", true, "rice controller config properties file");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("p", "printConfigItem", false, "Print all config item");
        opt.setRequired(false);
        options.addOption(opt);

        // 业务IP+端口， 选举端口默认是业务端口 -2
        opt =
            new Option("a", "allControllerAddressStr", true,
                "controller  address list, eg: 192.168.0.1:9876,192.168.0.2:9876");
        opt.setRequired(false);
        options.addOption(opt);

        // 业务IP+端口， 选举端口默认是业务端口 -2
        opt =
            new Option("m", "managePort", true,
                "controller  admin manager port, eg: 9090");
        opt.setRequired(false);
        options.addOption(opt);

        // 业务IP+端口， 选举端口默认是业务端口 -2
        opt =
            new Option("l", "localhost", true,
                "controller local host address, eg: 192.168.0.1");
        opt.setRequired(false);
        options.addOption(opt);
        return options;
    }
}
