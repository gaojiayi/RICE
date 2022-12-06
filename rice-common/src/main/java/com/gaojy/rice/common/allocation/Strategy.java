package com.gaojy.rice.common.allocation;

import java.util.List;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName Strategy.java
 * @Description 任务分配策略
 * @createTime 2022/01/18 22:38:00
 */
public interface Strategy {

    Map<String, List<String>> allocate(List<String> activeSchedulerServers, List<String> taskCodes);

    String allocateServer(List<String> activeSchedulerServers, String taskCode);

}
