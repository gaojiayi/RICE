package com.gaojy.rice.common.constants;

import java.util.Locale;

/**
 * @author gaojy
 * @ClassName ScheduleType.java
 * @Description
 * @createTime 2022/02/11 14:02:00
 */
public enum ScheduleType {
    CRON,
    FIXED_FREQUENCY,
    FIX_DELAY;

    public static ScheduleType getType(String type) {
        return ScheduleType.valueOf(type.toUpperCase());
    }
    }
