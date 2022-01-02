package com.gaojy.rice.remote.protocol;

/**
 * @author gaojy
 * @ClassName SerializeType.java
 * @Description TODO
 * @createTime 2022/01/03 00:18:00
 */
public enum SerializeType {
    JSON((byte) 0),
    RICE((byte) 1);

    private byte code;

    SerializeType(byte code) {
        this.code = code;
    }

    public static SerializeType valueOf(byte code) {
        for (SerializeType serializeType : SerializeType.values()) {
            if (serializeType.getCode() == code) {
                return serializeType;
            }
        }
        return null;
    }

    public byte getCode() {
        return code;
    }
}
