package com.gaojy.rice.dispatcher.common;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * @author gaojy
 * @ClassName TestHardware.java
 * @Description
 * @createTime 2022/08/22 21:32:00
 */
@RunWith(JUnit4.class)
public class TestHardware {

    @Test
    public void testHardware() throws Exception {
        System.out.println(HardwareHelper.getProcessCpuLoad());
        System.out.println(HardwareHelper.getMemoryRatio());
    }

}
