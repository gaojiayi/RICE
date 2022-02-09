package com.gaojy.rice.dispatcher.config;

import com.gaojy.rice.common.constants.ElectionConstants;

/**
 * @author gaojy
 * @ClassName DispatcherConfig.java
 * @Description TODO
 * @createTime 2022/02/09 20:28:00
 */
public class DispatcherConfig extends ElectionConstants {
    private String allControllerAddressStr = "";

    public String getAllControllerAddressStr() {
        return allControllerAddressStr;
    }

    public void setAllControllerAddressStr(String allControllerAddressStr) {
        this.allControllerAddressStr = allControllerAddressStr;
    }
}
