package com.gaojy.rice.remote.protocol;

import com.gaojy.rice.common.utils.StringUtil;
import com.gaojy.rice.remote.CommandCustomHeader;
import com.gaojy.rice.remote.common.RemoteHelper;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaojy
 * @ClassName RiceRemoteContext.java
 * @Description 作为RPC通信协议上下文
 * @createTime 2022/01/01 12:14:00
 */
public class RiceRemoteContext {
    private static final Logger log = LoggerFactory.getLogger(RemoteHelper.RICE_REMOTING);

    public static final String SERIALIZE_TYPE_PROPERTY = "rice.serialize.type";
    public static final String SERIALIZE_TYPE_ENV = "RICE_SERIALIZE_TYPE";
    public static final String REMOTING_VERSION_KEY = "rice.remoting.version";

    private static final int RPC_TYPE = 0; // 0, REQUEST_COMMAND  // 1, RESPONSE_COMMAND
    private static final int RPC_ONEWAY = 1; // 0, RPC  // 1, Oneway
    private int flag = 0;  // 用一个flag 记录 RPC_TYPE 和 RPC_ONEWAY

    /**
     * 缓存了具体的CommandCustomHeader都有哪些field
     */
    private static final Map<Class<? extends CommandCustomHeader>, Field[]> CLASS_HASH_MAP =
        new HashMap<Class<? extends CommandCustomHeader>, Field[]>();
    /**
     * 缓存具体的CommandCustomHeader对应的简单类名
     */
    private static final Map<Class, String> CANONICAL_NAME_CACHE = new HashMap<Class, String>();

    /**
     * 缓存一个field和对应的注解信息，用于后续解码后，数据的非空校验等
     */
    private static final Map<Field, Annotation> NOT_NULL_ANNOTATION_CACHE = new HashMap<Field, Annotation>();
    private static final String STRING_CANONICAL_NAME = String.class.getCanonicalName();
    private static final String DOUBLE_CANONICAL_NAME_1 = Double.class.getCanonicalName();
    private static final String DOUBLE_CANONICAL_NAME_2 = double.class.getCanonicalName();
    private static final String INTEGER_CANONICAL_NAME_1 = Integer.class.getCanonicalName();
    private static final String INTEGER_CANONICAL_NAME_2 = int.class.getCanonicalName();
    private static final String LONG_CANONICAL_NAME_1 = Long.class.getCanonicalName();
    private static final String LONG_CANONICAL_NAME_2 = long.class.getCanonicalName();
    private static final String BOOLEAN_CANONICAL_NAME_1 = Boolean.class.getCanonicalName();
    private static final String BOOLEAN_CANONICAL_NAME_2 = boolean.class.getCanonicalName();

    private static volatile int configVersion = -1;
    // 请求号计数器
    private static AtomicInteger requestId = new AtomicInteger(0);

    private static SerializeType serializeTypeConfigInThisServer = SerializeType.JSON;

    /**
     * 具体的请求编号
     */
    private int code;
    private LanguageCode language = LanguageCode.JAVA;
    private int version = 0;

    /**
     * 一次rpc的请求ID
     */
    private int opaque = requestId.getAndIncrement();

    /**
     * 异常信息记录在remark
     */
    private String remark;

    /**
     * 扩展字段，数据序列化前后存储结构
     */
    private HashMap<String, String> extFields;

    /**
     * 数据编码之前 把customHeader中的属性转化成extFields 再序列化
     * 数据反序列化之后 存在在extFields 再装换成具体的customHeader
     */
    private transient CommandCustomHeader customHeader;

    /**
     * 有rocketmq和json两种序列化方式，默认为json
     */
    private SerializeType serializeTypeCurrentRPC = serializeTypeConfigInThisServer;

    // 消息message，调用接口传入
    private transient byte[] body;

    static {
        final String protocol = System.getProperty(SERIALIZE_TYPE_PROPERTY, System.getenv(SERIALIZE_TYPE_ENV));
        if (!StringUtil.isBlank(protocol)) {
            try {
                serializeTypeConfigInThisServer = SerializeType.valueOf(protocol);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("parser specified protocol error. protocol=" + protocol, e);
            }
        }
    }

    public RiceRemoteContext() {
    }

    public static RiceRemoteContext createRequestCommand(int code, CommandCustomHeader customHeader) {
        RiceRemoteContext cmd = new RiceRemoteContext();
        cmd.setCode(code);
        cmd.customHeader = customHeader;
        setCmdVersion(cmd);
        return cmd;
    }

    private static void setCmdVersion(RiceRemoteContext cmd) {
        if (configVersion >= 0) {
            cmd.setVersion(configVersion);
        } else {
            String v = System.getProperty(REMOTING_VERSION_KEY);
            if (v != null) {
                int value = Integer.parseInt(v);
                cmd.setVersion(value);
                configVersion = value;
            }
        }
    }

    public static RiceRemoteContext createResponseCommand(Class<? extends CommandCustomHeader> classHeader) {
        return createResponseCommand(RemotingSysResponseCode.SYSTEM_ERROR, "not set any response code", classHeader);
    }

    public static RiceRemoteContext createResponseCommand(int code, String remark,
        Class<? extends CommandCustomHeader> classHeader) {
        RiceRemoteContext cmd = new RiceRemoteContext();
        cmd.markResponseType();
        cmd.setCode(code);
        cmd.setRemark(remark);
        setCmdVersion(cmd);

        if (classHeader != null) {
            try {
                CommandCustomHeader objectHeader = classHeader.newInstance();
                cmd.customHeader = objectHeader;
            } catch (InstantiationException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            }
        }

        return cmd;
    }

    public static RiceRemoteContext createResponseCommand(int code, String remark) {
        return createResponseCommand(code, remark, null);
    }


    public static int createNewRequestId() {
        for(int  i = requestId.incrementAndGet(); i < Integer.MAX_VALUE;){
            return i;
        }
        return requestId.getAndSet(0);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void markResponseType() {
        int bits = 1 << RPC_TYPE;
        this.flag |= bits;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
