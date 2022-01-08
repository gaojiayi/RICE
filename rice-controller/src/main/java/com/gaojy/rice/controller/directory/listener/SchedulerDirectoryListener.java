package com.gaojy.rice.controller.directory.listener;

import com.gaojy.rice.controller.directory.Directory;
import com.gaojy.rice.controller.directory.event.DirectoryEvent;

/**
 * @author gaojy
 * @ClassName SchedulerDirectoryListener.java
 * @Description TODO
 * @createTime 2022/01/09 00:02:00
 */
public class SchedulerDirectoryListener implements DirectoryListener {
    @Override public void handler(DirectoryEvent event) {
        // 事件源
        Directory source = event.getSource();

    }
}
