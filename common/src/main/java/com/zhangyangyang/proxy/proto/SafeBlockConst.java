package com.zhangyangyang.proxy.proto;

/**
 * Created by zhangyangyang on 2018/5/2.
 *
 * @see SafeBlock
 */
public final class SafeBlockConst {

    /**
     * 协议header
     */
    public static final String HEADER_1 = "safeBlock 1";
    public static final byte[] HEADER_1_BIN = HEADER_1.getBytes();

    /**
     * block type 申请建立连接
     * 传入用户名密码
     */
    public static final String TYPE_REQ_INIT = "1";
    public static final byte[] TYPE_REQ_INIT_BIN = TYPE_REQ_INIT.getBytes();

    /**
     * block type 发送目标连接，指定host 和 port
     * 传入host 和 port
     */
    public static final String TYPE_REQ_CONN = "2";
    public static final byte[] TYPE_REQ_CONN_BIN = TYPE_REQ_CONN.getBytes();

    /**
     * block type 数据传输
     */
    public static final String TYPE_REQ_DATA = "3";
    public static final byte[] TYPE_REQ_DATA_BIN = TYPE_REQ_DATA.getBytes();

    /**
     * block type
     * 响应成功
     */
    public static final String TYPE_RES_SUCCESS = "4";
    public static final byte[] TYPE_RES_SUCCESS_BIN = TYPE_RES_SUCCESS.getBytes();


    /**
     * block type
     * 响应失败
     */
    public static final String TYPE_RES_FAILED = "5";
    public static final byte[] TYPE_RES_FAILED_BIN = TYPE_RES_FAILED.getBytes();


}
