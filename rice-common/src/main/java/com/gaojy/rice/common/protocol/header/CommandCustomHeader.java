package com.gaojy.rice.common.protocol.header;

import com.gaojy.rice.common.exception.RemotingCommandException;

/**
 * @author gaojy
 * @ClassName CommandCustomHeader.java
 * @Description TODO
 * @createTime 2022/01/03 00:30:00
 */
public interface CommandCustomHeader {
    void checkFields() throws RemotingCommandException;

}
