package com.gaojy.rice.controller.directory;

import com.gaojy.rice.controller.directory.event.DirectoryEvent;

/**
 * @author gaojy
 * @ClassName Directory.java
 * @Description
 * @createTime 2022/01/08 16:33:00
 */
public interface Directory {

    /**
     * @description 删除节点
     */
    public void destroy();

    public void  addDirectory(Directory directory);

    public void triggerListener(DirectoryEvent event);


}
