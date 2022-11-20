package com.gaojy.rice.repository.api;

import com.gaojy.rice.common.entity.RiceAppInfo;
import com.gaojy.rice.common.extension.SPI;
import com.gaojy.rice.repository.api.dao.ProcessorServerInfoDao;
import com.gaojy.rice.repository.api.dao.RiceAppInfoDao;
import com.gaojy.rice.repository.api.dao.RiceLogDao;
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
    public static final String REPOSITORY_TYPE_KEY = "repository.type";

    public static final String REPOSITORY_URL_KEY = "repository.url";

    public static final String REPOSITORY_USERNAME_KEY = "repository.username";

    public static final String REPOSITORY_PASSWORD_KEY = "repository.password";

    public void connect();

    public void close();

    public RiceTaskChangeRecordDao getRiceTaskChangeRecordDao();

    public ProcessorServerInfoDao getProcessorServerInfoDao();

    public RiceTaskInfoDao getRiceTaskInfoDao();

    public TaskInstanceInfoDao getTaskInstanceInfoDao();

    public RiceLogDao getRiceLogDao();

    public RiceAppInfoDao getRiceAppInfoDao();
}
