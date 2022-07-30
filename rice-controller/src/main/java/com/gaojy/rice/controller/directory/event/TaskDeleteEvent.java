package com.gaojy.rice.controller.directory.event;

import com.gaojy.rice.controller.directory.Directory;

/**
 * @author gaojy
 * @ClassName TaskDeleteEvent.java
 * @Description
 * @createTime 2022/01/08 23:57:00
 */
public class TaskDeleteEvent extends DirectoryEvent {

    public TaskDeleteEvent(Directory source) {
        super.setSource(source);
    }
}
