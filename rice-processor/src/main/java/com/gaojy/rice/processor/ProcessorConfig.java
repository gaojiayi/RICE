package com.gaojy.rice.processor;

import com.gaojy.rice.processor.log.RiceClientLogger;
import com.gaojy.rice.remote.transport.TransfSystemConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
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



    private int listenPort = 8888;
    private int serverWorkerThreads = 8;
    private int serverCallbackExecutorThreads = 0;
    private int serverSelectorThreads = 3;
    private int serverOnewaySemaphoreValue = 256;
    private int serverAsyncSemaphoreValue = 64;
    private int serverChannelMaxIdleTimeSeconds = 120;

    private int serverSocketSndBufSize = TransfSystemConfig.socketSndbufSize;
    private int serverSocketRcvBufSize = TransfSystemConfig.socketRcvbufSize;
    private boolean serverPooledByteBufAllocatorEnable = true;

    private boolean useEpollNativeSelector = false;

    public ProcessorConfig() {
        try {
            in = ProcessorConfig.class.getClassLoader().getResourceAsStream(DEFAULT_RICE_CONFIG_FILE_PATH);
            p.load(in);
            initServerConfig();
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
    }

    private void initServerConfig() {
        if (p.getProperty("rice.export.port") == null) {
            if (System.getProperty("rice.export.port") != null) {
                listenPort = Integer.parseInt(System.getProperty("rice.export.port"));
            }
        } else {
            listenPort = Integer.parseInt(p.getProperty("rice.export.port"));
        }


    }

}
