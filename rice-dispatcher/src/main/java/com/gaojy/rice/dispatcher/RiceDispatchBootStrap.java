package com.gaojy.rice.dispatcher;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.gaojy.rice.common.constants.LoggerName;
import com.gaojy.rice.common.utils.CommandUtil;
import com.gaojy.rice.common.utils.MixAll;
import com.gaojy.rice.common.utils.RiceBanner;
import com.gaojy.rice.dispatcher.config.DispatcherConfig;
import com.gaojy.rice.remote.protocol.RiceRemoteContext;
import com.gaojy.rice.remote.transport.TransfClientConfig;
import com.gaojy.rice.repository.api.Repository;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName RiceDispatchBootStrap.java
 * @Description
 * @createTime 2022/08/15 21:51:00
 */
public class RiceDispatchBootStrap {
    private static CommandLine commandLine = null;
    private static Properties properties = null;

    public static void main(String[] args) {
        main0(args);
    }
    public static void main0(String[] args){
        Options options = CommandUtil.buildCommandlineOptions(new Options());
        commandLine = CommandUtil.parseCmdLine("rice-dispatcher", args, buildCommandlineOptions(options), new PosixParser());
        if (null == commandLine) {
            System.exit(-1);
        }
        DispatcherConfig config = new DispatcherConfig();
        if (null == config.getRiceHome()) {
            System.out.printf("Please set the " + MixAll.RICE_HOME_ENV + " variable in your environment to match the location of the RocketMQ installation%n");
            System.exit(-2);
        }

        String file = config.getRiceHome() + File.separator + "conf" + File.separator + "rice-dispatcher.properties";
        //  优先级最低的配置是文件
        if (commandLine.hasOption('c')) {
            file = commandLine.getOptionValue('f');
        }
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            properties = new Properties();
            properties.load(in);
            MixAll.properties2Object(properties, config);
            config.setConfigStorePath(file);

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

            if (commandLine.hasOption('p')) {
                MixAll.printObjectProperties(null, config);
                System.exit(0);
            }
            // 优先级其次是来自命令行的参数
            MixAll.properties2Object(CommandUtil.commandLine2Properties(commandLine), config);
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            lc.reset();
            configurator.doConfigure(config.getRiceHome() + File.separator + "conf"
                + File.separator + "logback_dispatcher.xml");
            final Logger log = LoggerFactory.getLogger(LoggerName.CONTROLLER_LOGGER_NAME);

            MixAll.printObjectProperties(log, config);

            final RiceDispatchScheduler scheduler = new RiceDispatchScheduler(new TransfClientConfig(),config);

            try {
                scheduler.start();
                // 打印banner
                RiceBanner.show(7);
                String tip = "The RICE Dispatcher boot success. serializeType=" + RiceRemoteContext.getSerializeTypeConfigInThisServer();
                log.info(tip);
                System.out.printf(tip + "%n");
            } catch (Exception e) {
                scheduler.shutdown();
                System.exit(-3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }
    private static Options buildCommandlineOptions(final Options options) {
        // 配置文件地址
        Option opt = new Option("f", "configFile", true, "rice dispatcher config properties file");
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

        // 业务IP+端口， 选举端口默认是业务端口 -2 暂时不使用JMX
        opt =
            new Option("j", "JMXManagePort", true,
                "dispatcher jmx manager port, eg: 9090");
        opt.setRequired(false);
        options.addOption(opt);
        return options;
    }
}
