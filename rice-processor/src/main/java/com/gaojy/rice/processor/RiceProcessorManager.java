package com.gaojy.rice.processor;

import com.gaojy.rice.common.utils.RiceBanner;
import com.gaojy.rice.processor.log.RiceClientLogger;
import com.gaojy.rice.remote.IBaseRemote;
import com.gaojy.rice.remote.transport.TransportServer;
import org.slf4j.Logger;

/**
 * @author gaojy
 * @ClassName RiceProcessorManager.java
 * @Description RICE 处理器管理类
 * @createTime 2022/01/04 20:56:00
 */
public class RiceProcessorManager {
    Logger log = RiceClientLogger.getLog();
    private static RiceProcessorManager manager = null;
    private static Object monitorObj = new Object();

    private final IBaseRemote transportServer;

    private RiceProcessorManager() {
        transportServer = null;
    }

    public static RiceProcessorManager getManager() {
        if (manager == null) {
            synchronized (monitorObj) {
                if (manager == null) {
                    manager = new RiceProcessorManager();
                    return manager;
                }
            }
        }
        return manager;
    }

    public void export() {
        // 收集所有的task 并缓存起来

        //  校验task 有没有重复的task

        // 注册业务处理器  启动监听

        // 创建网络事件监听器

        // 将所有的task包装成请求对象  并暴露给控制器

        // 如果控制器成功返回 打印banner

        // 如果返回失败  则 destory 并 抛出异常
        RiceBanner.show(7);
    }

    public void destory() {

    }

    public static void main(String[] args) {
        RiceProcessorManager.getManager().export();
    }
}
