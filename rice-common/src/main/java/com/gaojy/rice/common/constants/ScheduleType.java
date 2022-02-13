package com.gaojy.rice.common.constants;

import java.util.Locale;

/**
 * @author gaojy
 * @ClassName ScheduleType.java
 * @Description TODO
 * @createTime 2022/02/11 14:02:00
 */
public enum ScheduleType {
    CRON,
    FIX_RATE,
    FIX_DELAY;

    public static ScheduleType getType(String type) {
        return ScheduleType.valueOf(type.toUpperCase());
    }
    }
