package com.gaojy.rice.processor.api.config;

import com.gaojy.rice.common.constants.ElectionConstants;
import com.gaojy.rice.common.exception.ProcessorException;
import com.gaojy.rice.common.utils.StringUtil;
import com.gaojy.rice.processor.api.log.RiceClientLogger;
import com.gaojy.rice.remote.transport.TransfClientConfig;
import com.gaojy.rice.remote.transport.TransfServerConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.slf4j.Logger;

/**
 * @author gaojy
 * @ClassName ProcessorConfig.java
 * @Description 配置类
 * @createTime 2022/01/04 20:03:00
 */
public class ProcessorConfig extends ElectionConstants {
    final Logger log = RiceClientLogger.getLog();
    public static final String RICE_PRO_CONFIG_FILE_PATH_KEY = "rice.processor.config.file.path";
    public static final String DEFAULT_RICE_CONFIG_FILE_PATH = "rice.properties";
    public static final String RICE_PROCESSOR_SCAN_PACKAGE_KEY = "rice.processor.scan.package";
    public static final String RICE_PRO_CONFIG_FILE_PATH = System.getProperty(RICE_PRO_CONFIG_FILE_PATH_KEY
        , DEFAULT_RICE_CONFIG_FILE_PATH);
    private Properties p = null;
    private transient InputStream in = null;
    //private Map<TaskDetailData, Class<? extends RiceBasicProcessor>> processorMap = new HashMap<>();

    private boolean serverPooledByteBufAllocatorEnable = true;

    private TransfClientConfig transfClientConfig;

    private TransfServerConfig transfServerConfig;

    private String appId;

    private String controllerServers;

    private String balancePolicy;

    public ProcessorConfig() {
        try {
            in = ProcessorConfig.class.getClassLoader().getResourceAsStream(RICE_PRO_CONFIG_FILE_PATH);
            p = new Properties();
            p.load(in);
        } catch (IOException e) {
            log.warn("Failed to load rice processor config, please checkout config path [" + RICE_PRO_CONFIG_FILE_PATH + "]");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("Failed to cloe rice processor config, " + e);
                }
            }
        }
        if (p == null) {
            throw new ProcessorException("Not found processor config in path [" + RICE_PRO_CONFIG_FILE_PATH + "]");
        }
        controllerServers = p.getProperty(ConfigConstants.RICE_CONTROLLER_ADDRESS);
        appId = p.getProperty(ConfigConstants.RICE_APPLICATION_ID);
        balancePolicy = p.getProperty(ConfigConstants.RICE_CONTROLLER_BALANCE);
        initRPCConfig();
        //loadProcessor();
    }

//    private void loadProcessor() {
//        String packeages = p.getProperty(ConfigConstants.RICE_PROCESSOR_SCAN_PACKAGES);
//        Arrays.stream(packeages.split(",")).forEach(p -> {
//            PackageScanner scan = new ClasspathPackageScanner(p);
//            try {
//                scan.getFullyQualifiedClassNameList().stream().forEach(clazzName -> {
//                    try {
//                        Class clazz = Class.forName(clazzName);
//                        String taskCode = "";
//                        String taskName = "";
//                        boolean logEnable = false;
//                        if (clazz.isAssignableFrom(RiceBasicProcessor.class)) {
//                            // TODO获取task  根据注解 或者任务名称
//                            if (clazz.getAnnotation(Executer.class) != null) {
//                                Executer annotation = (Executer) clazz.getAnnotation(Executer.class);
//                                taskCode = annotation.taskCode();
//                                taskName = annotation.taskName();
//
//                            } else {
//                                taskName = taskCode = clazz.getSimpleName();
//                            }
//                           if (clazz.getAnnotation(LogEnable.class) != null) {
//                               logEnable = true;
//                           }
//                            TaskDetailData detailData = new TaskDetailData(taskCode, taskName, clazzName, logEnable);
//                            Class prevProcessor = this.processorMap.putIfAbsent(detailData, clazz);
//                            if (prevProcessor != null) {
//                                throw new DuplicateProcessorException("The same task prpcessor exist,taskcode=" + taskCode);
//                            }
//                        }
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//    }

    private void initRPCConfig() {
        transfClientConfig = new TransfClientConfig();
        transfServerConfig = new TransfServerConfig();
        if (StringUtil.isEmpty(p.getProperty(ConfigConstants.LISTEN_PORT))) { // 如果在配置文件中没有找到端口配置则使用默认端口
            int listenPort = Integer.parseInt(System.getProperty(ConfigConstants.LISTEN_PORT, ConfigConstants.DEFAULT_LISTEN_PORT));
            transfServerConfig.setListenPort(listenPort);

        } else {
            int listenPort = Integer.parseInt(p.getProperty(ConfigConstants.LISTEN_PORT));
            transfServerConfig.setListenPort(listenPort);

        }

        if (StringUtil.isNotEmpty(p.getProperty(ConfigConstants.SERVER_ASYNC_SEMAPHORE_VALUE))) {
            transfServerConfig.setServerAsyncSemaphoreValue(
                Integer.parseInt(p.getProperty(ConfigConstants.SERVER_ASYNC_SEMAPHORE_VALUE)));
        }
        if (StringUtil.isNotEmpty(p.getProperty(ConfigConstants.SERVER_ONEWAY_SEMAPHORE_VALUE))) {
            transfServerConfig.setServerOnewaySemaphoreValue(
                Integer.parseInt(p.getProperty(ConfigConstants.SERVER_ONEWAY_SEMAPHORE_VALUE)));
        }
        if (StringUtil.isNotEmpty(p.getProperty(ConfigConstants.SERVER_WORKER_THREADS))) {
            transfServerConfig.setServerWorkerThreads(
                Integer.parseInt(p.getProperty(ConfigConstants.SERVER_WORKER_THREADS)));
        }
        if (StringUtil.isNotEmpty(p.getProperty(ConfigConstants.SERVER_SELECTOR_THREADS))) {
            transfServerConfig.setServerSelectorThreads(
                Integer.parseInt(p.getProperty(ConfigConstants.SERVER_SELECTOR_THREADS)));
        }
        if (StringUtil.isNotEmpty(p.getProperty(ConfigConstants.SERVER_CALLBACK_EXECUTOR_THREADS))) {
            transfServerConfig.setServerCallbackExecutorThreads(
                Integer.parseInt(p.getProperty(ConfigConstants.SERVER_CALLBACK_EXECUTOR_THREADS)));
        }
        if (StringUtil.isNotEmpty(p.getProperty(ConfigConstants.SERVER_CHANNEL_MAXIDLE_TIME_SECONDS))) {
            transfServerConfig.setServerChannelMaxIdleTimeSeconds(
                Integer.parseInt(p.getProperty(ConfigConstants.SERVER_CHANNEL_MAXIDLE_TIME_SECONDS)));
        }
        if (StringUtil.isNotEmpty(p.getProperty(ConfigConstants.SERVER_POOLED_BYTEBUF_ALLOCATOR_ENABLE))) {
            transfServerConfig.setServerPooledByteBufAllocatorEnable(
                Boolean.parseBoolean(p.getProperty(ConfigConstants.SERVER_POOLED_BYTEBUF_ALLOCATOR_ENABLE)));
        }

    }

    public String getAppId() {
        return appId;
    }

//    public void setAppId(String appId) {
//        this.appId = appId;
//    }

//    public Map<TaskDetailData, Class<? extends RiceBasicProcessor>> getProcessorMap() {
//        return processorMap;
//    }
//
//    public void setProcessorMap(
//        Map<TaskDetailData, Class<? extends RiceBasicProcessor>> processorMap) {
//        this.processorMap = processorMap;
//    }

    public TransfClientConfig getTransfClientConfig() {
        return transfClientConfig;
    }

    public void setTransfClientConfig(TransfClientConfig transfClientConfig) {
        this.transfClientConfig = transfClientConfig;
    }

    public TransfServerConfig getTransfServerConfig() {
        return transfServerConfig;
    }

    public void setTransfServerConfig(TransfServerConfig transfServerConfig) {
        this.transfServerConfig = transfServerConfig;
    }

    public List<String> getControllerServerList() {
        if (StringUtil.isNotEmpty(controllerServers)) {
            return Arrays.asList(controllerServers.split(","));
        }
        return null;
    }

    public String getControllerServers() {
        return controllerServers;
    }

    public List<String> getTaskPackage() {
        String packages = p.getProperty(ConfigConstants.RICE_PROCESSOR_SCAN_PACKAGE_KEY);
        if (StringUtil.isNotEmpty(packages)) {
            return Arrays.asList(packages.split(","));
        }
        return null;
    }

    public String getBalancePolicy() {
        return balancePolicy;
    }
}
