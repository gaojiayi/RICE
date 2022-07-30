package com.gaojy.rice.common.protocol.header.processor;

import com.gaojy.rice.common.annotation.CFNotNull;
import com.gaojy.rice.common.exception.RemotingCommandException;
import com.gaojy.rice.common.protocol.header.CommandCustomHeader;

/**
 * @author gaojy
 * @ClassName ExportTaskRequestHeader.java
 * @Description 
 * @createTime 2022/01/02 14:21:00
 */
public class ExportTaskRequestHeader implements CommandCustomHeader {

    @CFNotNull
    private String appId;

    @CFNotNull
    private int listenPort;

    @CFNotNull
    private String netAddress;

    private Long registerTime = System.currentTimeMillis();

    @Override public void checkFields() throws RemotingCommandException {

    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public String getNetAddress() {
        return netAddress;
    }

    public void setNetAddress(String netAddress) {
        this.netAddress = netAddress;
    }
}
