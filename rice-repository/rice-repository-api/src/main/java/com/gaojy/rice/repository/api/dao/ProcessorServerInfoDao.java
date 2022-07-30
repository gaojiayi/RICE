package com.gaojy.rice.repository.api.dao;

import com.gaojy.rice.common.exception.RepositoryException;
import com.gaojy.rice.common.entity.ProcessorServerInfo;
import java.util.List;

/**
 * @author gaojy
 * @ClassName ProcessorServerInfo.java
 * @Description 
 * @createTime 2022/01/27 21:24:00
 */
public interface ProcessorServerInfoDao {
    public List<ProcessorServerInfo> getInfosByServer(String address, int port) throws RepositoryException;

    public int batchCreateOrUpdateInfo(List<ProcessorServerInfo> processorServerInfoList) throws RepositoryException;

    public List<ProcessorServerInfo>  getInfosByTask(String taskCode);

}
