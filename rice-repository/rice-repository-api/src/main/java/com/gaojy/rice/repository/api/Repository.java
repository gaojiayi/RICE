package com.gaojy.rice.repository.api;

import com.gaojy.rice.common.entity.TaskInstanceInfo;
import com.gaojy.rice.common.extension.SPI;
import com.gaojy.rice.repository.api.dao.ProcessorServerInfoDao;
import com.gaojy.rice.repository.api.dao.RiceTaskChangeRecordDao;
import com.gaojy.rice.repository.api.dao.RiceTaskInfoDao;
import com.gaojy.rice.repository.api.dao.TaskInstanceInfoDao;

/**
 * @author gaojy
 * @ClassName Repository.java
 * @Description 
 * @createTime 2022/01/17 17:11:00
 */
@SPI
public interface Repository {

    public void connect();

    public void close();

    public RiceTaskChangeRecordDao getRiceTaskChangeRecordDao();

    public ProcessorServerInfoDao getProcessorServerInfoDao();

    public RiceTaskInfoDao getRiceTaskInfoDao();

    public TaskInstanceInfoDao getTaskInstanceInfoDao();
}
