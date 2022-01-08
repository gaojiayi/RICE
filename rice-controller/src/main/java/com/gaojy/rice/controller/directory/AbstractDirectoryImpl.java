package com.gaojy.rice.controller.directory;

import com.gaojy.rice.controller.directory.event.DirectoryEvent;
import com.gaojy.rice.controller.directory.listener.DirectoryListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author gaojy
 * @ClassName AbstractDirectoryImpl.java
 * @Description TODO
 * @createTime 2022/01/08 20:41:00
 */
public abstract class AbstractDirectoryImpl {

    private String directoryName;

    private List<? extends Directory> nextDirectory = new LinkedList<>();

    private DirectoryType directoryType;

    private Map<String, String> data = new HashMap<>();

    private DirectoryListener listener;

    enum DirectoryType {
        PERSISTENCE,// 不能删除
        TRANSIENT
    }

    public void triggerListener(final DirectoryEvent event) {
        if (listener != null) {
            listener.handler(event);
        }
        nextDirectory.stream().forEach(d -> {
            d.triggerListener(event);
        });

    }
}
