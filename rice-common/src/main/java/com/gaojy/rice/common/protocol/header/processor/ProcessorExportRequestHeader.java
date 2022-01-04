package com.gaojy.rice.common.protocol.header.processor;

import com.gaojy.rice.common.annotation.CFNotNull;
import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;

/**
 * @author gaojy
 * @ClassName ProcessorExportRequestHeader.java
 * @Description 处理器任务暴露请求
 * @createTime 2022/01/04 19:11:00
 */
public class ProcessorExportRequestHeader implements CommandCustomHeader {

    @CFNotNull
    public String taskCode;

    @CFNotNull
    public String appId;


    @Override
    public void checkFields() throws RemotingCommandException {

    }
}
