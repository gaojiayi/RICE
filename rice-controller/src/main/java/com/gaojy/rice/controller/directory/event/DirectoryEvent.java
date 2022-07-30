package com.gaojy.rice.controller.directory.event;

import com.gaojy.rice.controller.directory.Directory;

/**
 * @author gaojy
 * @ClassName DirectoryEvent.java
 * @Description 
 * @createTime 2022/01/08 23:58:00
 */
public abstract class DirectoryEvent {
    private Directory source;

    public Directory getSource() {
        return source;
    }

    public void setSource(Directory source) {
        this.source = source;
    }
}
