package com.gaojy.rice.common.constants;

public enum TaskInstanceStatus {
    WAIT(0),RUNNING(1),FINISHED(2),TIMEOUT(3),EXCEPTION(4);

    private int code;

    TaskInstanceStatus(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
