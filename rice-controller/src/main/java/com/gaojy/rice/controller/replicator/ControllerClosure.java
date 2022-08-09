package com.gaojy.rice.controller.replicator;

import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Status;

/**
 * @author gaojy
 * @ClassName ControllerClosure.java
 * @Description 回调处理器
 * @createTime 2022/08/08 10:56:00
 */
public abstract class ControllerClosure implements Closure {
    private ControllerOperation controllerOperation;

    public ControllerOperation getControllerOperation() {
        return controllerOperation;
    }

    public void setControllerOperation(ControllerOperation controllerOperation) {
        this.controllerOperation = controllerOperation;
    }
}
