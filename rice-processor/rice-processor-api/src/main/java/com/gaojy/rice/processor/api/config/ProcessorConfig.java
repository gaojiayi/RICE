package com.gaojy.rice.processor.api.config;

import com.gaojy.rice.common.TaskType;
import com.gaojy.rice.common.exception.DuplicateProcessorException;
import com.gaojy.rice.common.protocol.body.processor.TaskDetailData;
import com.gaojy.rice.common.utils.StringUtil;
import com.gaojy.rice.processor.api.RiceBasicProcessor;
import com.gaojy.rice.processor.api.annotation.Executer;
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
public class ProcessorConfig {
    Logger log = RiceClientLogger.getLog();
    public static final String RICE_PRO_CONFIG_FILE_PATH_KEY = "rice.processor.config.file.path";
    public static final String DEFAULT_RICE_CONFIG_FILE_PATH = "rice.properties";
    public static final String RICE_PRO_CONFIG_FILE_PATH = System.getProperty(RICE_PRO_CONFIG_FILE_PATH_KEY
            , DEFAULT_RICE_CONFIG_FILE_PATH);
    private final Properties p = new Properties();
    private transient InputStream in = null;
    private Map<TaskDetailData, Class<? extends RiceBasicProcessor>> processorMap = new HashMap<>();

    private boolean serverPooledByteBufAllocatorEnable = true;

    private TransfClientConfig transfClientConfig;

    private TransfServerConfig transfServerConfig;

    private String appId;

    private String controllerServers;


    public ProcessorConfig() {
        try {
            in = ProcessorConfig.class.getClassLoader().getResourceAsStream(DEFAULT_RICE_CONFIG_FILE_PATH);
            p.load(in);
        } catch (IOException e) {
            log.warn("Failed to load rice processor config, will use default config " + e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("Failed to cloe rice processor config, " + e);
                }
            }
        }
        initRPCConfig();
        loadProcessor();
    }

    private void loadProcessor() {
        String packeages = p.getProperty(ConfigConstants.RICE_PROCESSOR_SCAN_PACKAGES);
        Arrays.stream(packeages.split(",")).forEach(p -> {
            PackageScanner scan = new ClasspathPackageScanner(p);
            try {
                scan.getFullyQualifiedClassNameList().stream().forEach(clazzName -> {
                    try {
                        Class clazz = Class.forName(clazzName);
                        String taskCode = "";
                        String taskName = "";
                        if (clazz.isAssignableFrom(RiceBasicProcessor.class)) {
                            // TODO获取task  根据注解 或者任务名称
                            if (clazz.getAnnotation(Executer.class) != null) {
                                Executer annotation = (Executer) clazz.getAnnotation(Executer.class);
                                taskCode = annotation.taskCode();
                                taskName = annotation.taskName();

                            } else {
                                taskName = taskCode = clazz.getSimpleName();
                            }
                            TaskType taskType = TaskType.RICE_BASE_TASK_TYPE;
                            if (clazz.getSuperclass().getSimpleName().toLowerCase().indexOf("mapreduce") > 0) {
                                taskType = TaskType.RICE_MAP_REDUCE_TYPE;
                            }
                            TaskDetailData detailData = new TaskDetailData(taskCode, taskName, clazzName, taskType);
                            Class prevProcessor = this.processorMap.putIfAbsent(detailData, clazz);
                            if (prevProcessor != null) {
                                throw new DuplicateProcessorException("The same task prpcessor exist,taskcode=" + taskCode);
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void initRPCConfig() {
        transfClientConfig = new TransfClientConfig();
        transfServerConfig = new TransfServerConfig();
        if (StringUtil.isEmpty(p.getProperty(ConfigConstants.LISTEN_PORT))) {
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
        // TODO Other service Config

    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Map<TaskDetailData, Class<? extends RiceBasicProcessor>> getProcessorMap() {
        return processorMap;
    }

    public void setProcessorMap(
            Map<TaskDetailData, Class<? extends RiceBasicProcessor>> processorMap) {
        this.processorMap = processorMap;
    }

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


}
