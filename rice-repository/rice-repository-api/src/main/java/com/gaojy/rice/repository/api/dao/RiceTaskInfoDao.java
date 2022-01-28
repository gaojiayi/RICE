package com.gaojy.rice.repository.api.dao;

import com.gaojy.rice.repository.api.entity.RiceTaskInfo;
import java.util.List;

/**
 * @author gaojy
 * @ClassName RiceTaskInfoDao.java
 * @Description TODO
 * @createTime 2022/01/17 17:09:00
 */
public interface RiceTaskInfoDao {

   List<RiceTaskInfo> getInfoByCodes(List<String> taskCodes);
    

}
