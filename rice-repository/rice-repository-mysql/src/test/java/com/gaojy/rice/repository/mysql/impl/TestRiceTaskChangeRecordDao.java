package com.gaojy.rice.repository.mysql.impl;

import com.gaojy.rice.common.constants.TaskOptType;
import com.gaojy.rice.common.entity.TaskChangeRecord;
import com.gaojy.rice.repository.api.dao.RiceTaskChangeRecordDao;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author gaojy
 * @ClassName TestRiceTaskChangeRecordDao.java
 * @Description
 * @createTime 2022/08/14 13:47:00
 */
@RunWith(JUnit4.class)
public class TestRiceTaskChangeRecordDao {

    RiceTaskChangeRecordDao dao = new RiceTaskChangeRecordDaoImpl();

    @Test
    public void testRecord() {
        List<TaskChangeRecord> records = new ArrayList<>();
        long task_code_0_create_time = 0L;
        for (int i = 0; i < 3; i++) {
            TaskChangeRecord taskChangeRecord = new TaskChangeRecord();
            taskChangeRecord.setTaskCode("TEST_TASK_CODE_" + i);
            taskChangeRecord.setOptType(TaskOptType.TASK_RUN.getCode());
            taskChangeRecord.setCreateTime(new Date(System.currentTimeMillis() + i * 1000 * 60 * 60));
            if (i == 0) {
                task_code_0_create_time = taskChangeRecord.getCreateTime().getTime();
            }
            records.add(taskChangeRecord);
        }

        dao.insert(records);
        long test_task_code_0 = dao.getLatestRecord("TEST_TASK_CODE_0");
        Assert.assertEquals(task_code_0_create_time, test_task_code_0);

        List<TaskChangeRecord> test_task_code_1_exist = dao.getChanges("TEST_TASK_CODE_1", System.currentTimeMillis());
        Assert.assertEquals(test_task_code_1_exist.size(), 1);

        List<TaskChangeRecord> test_task_code_1_not_found = dao.getChanges("TEST_TASK_CODE_1",
            task_code_0_create_time + 3 * 1000 * 60 * 60);
        Assert.assertEquals(test_task_code_1_not_found.size(), 0);

    }
}
