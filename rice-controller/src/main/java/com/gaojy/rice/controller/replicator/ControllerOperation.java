package com.gaojy.rice.controller.replicator;

import java.io.Serializable;

/**
 * @author gaojy
 * @ClassName ControllerOperation.java
 * @Description
 * @createTime 2022/08/05 23:18:00
 */
public class ControllerOperation implements Serializable {
    private static final long serialVersionUID = -6597003954824547294L;
    /**
     * Get value
     */
    public static final byte GET = 0x01;
    /**
     * Update value
     */
    public static final byte UPDATE = 0x02;

    private byte op;

    private String schedulerAddress;

    private SchedulerData schedulerData;

    public static ControllerOperation createGet() {
        return new ControllerOperation(GET);
    }

    public static ControllerOperation createUpdate(String schedulerAddress, SchedulerData data) {
        return new ControllerOperation(UPDATE, schedulerAddress, data);
    }

    public ControllerOperation(byte op) {
        this(op, "", null);
    }

    public ControllerOperation(byte op, String schedulerAddress, SchedulerData data) {
        this.op = op;
        this.schedulerAddress = schedulerAddress;
        this.schedulerData = data;
    }

    public byte getOp() {
        return op;
    }

    public String getSchedulerAddress() {
        return schedulerAddress;
    }

    public void setSchedulerAddress(String schedulerAddress) {
        this.schedulerAddress = schedulerAddress;
    }

    public SchedulerData getSchedulerData() {
        return schedulerData;
    }

    public void setSchedulerData(SchedulerData schedulerData) {
        this.schedulerData = schedulerData;
    }
}
