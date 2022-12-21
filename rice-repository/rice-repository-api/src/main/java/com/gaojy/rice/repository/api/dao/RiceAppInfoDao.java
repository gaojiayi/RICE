package com.gaojy.rice.repository.api.dao;

import com.gaojy.rice.common.entity.RiceAppGroupInfo;
import com.gaojy.rice.common.entity.RiceAppInfo;
import java.util.List;

/**
 * @author gaojy
 * @ClassName RiceAppInfoDao.java
 * @Description
 * @createTime 2022/11/20 14:12:00
 */
public interface RiceAppInfoDao {

    public List<RiceAppGroupInfo> getCountByName(Integer limit);

    public List<RiceAppInfo> queryApps(String appName, Integer pageIndex, Integer pageSize);
    public List<RiceAppInfo> queryAppsByIds(List<Long> ids);
    public Integer queryAppsCount(String appName);

    public void createApp(RiceAppInfo appInfo);

    public void deleteAppById(Long id);
}
