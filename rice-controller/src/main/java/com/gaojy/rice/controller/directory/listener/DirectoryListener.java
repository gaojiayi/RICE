package com.gaojy.rice.controller.directory.listener;

import com.gaojy.rice.controller.directory.event.DirectoryEvent;

/**
 * @author gaojy
 * @ClassName DirectoryListener.java
 * @Description TODO
 * @createTime 2022/01/08 23:42:00
 */
public interface DirectoryListener {

    public void handler(DirectoryEvent event);
}
