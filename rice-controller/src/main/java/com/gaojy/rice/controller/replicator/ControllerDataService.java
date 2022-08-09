package com.gaojy.rice.controller.replicator;

import com.alipay.sofa.jraft.Closure;
import java.util.List;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName ControllerDataService.java
 * @Description
 * @createTime 2022/08/01 16:15:00
 */
public interface ControllerDataService {

    public List<Map<String,String>> querySchedulersData(final boolean readOnlySafe, final ControllerClosure closure);

    public void updateSchedulerData(String schedulerAddress, SchedulerData data, final ControllerClosure closure);

}
