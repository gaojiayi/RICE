package com.gaojy.rice.dispatcher.common;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * @author gaojy
 * @ClassName HardwareHelper.java
 * @Description 系统状态
 * @createTime 2022/08/22 21:14:00
 */
public class HardwareHelper {

    private static final SystemInfo si = new SystemInfo();

    public static double getProcessCpuLoad(){
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor cpu = hal.getProcessor();
        return BigDecimal.valueOf(cpu.getSystemLoadAverage())
            .setScale(2, RoundingMode.HALF_UP).doubleValue();

    }

    public static double getMemoryRatio() {
        HardwareAbstractionLayer hal = si.getHardware();
        GlobalMemory mem = hal.getMemory();
        long usedMemory = mem.getTotal() - mem.getAvailable();
        return BigDecimal.valueOf(usedMemory * 100)
            .divide(BigDecimal.valueOf(mem.getTotal()),
                2, RoundingMode.HALF_UP).doubleValue();

    }

}


