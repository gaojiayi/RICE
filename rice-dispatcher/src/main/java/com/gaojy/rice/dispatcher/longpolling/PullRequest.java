package com.gaojy.rice.dispatcher.longpolling;

import com.gaojy.rice.repository.api.dao.RiceTaskChangeRecordDao;
import com.gaojy.rice.repository.api.entity.RiceTaskInfo;
import com.gaojy.rice.repository.api.entity.TaskChangeRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaojy
 * @ClassName PullRequest.java
 * @Description TODO
 * @createTime 2022/02/09 14:08:00
 */
public class PullRequest {

    private Long lastTaskChangeTimestamp = -1l;

    public Long getLastTaskChangeTimestamp() {
        return lastTaskChangeTimestamp;
    }

    public void setLastTaskChangeTimestamp(Long lastTaskChangeTimestamp) {
        this.lastTaskChangeTimestamp = lastTaskChangeTimestamp;
    }
}
