package com.zhangyangyang.proxy.server;

import com.zhangyangyang.proxy.common.ResCode;

/**
 * Created by zhangyangyang on 2018/3/28.
 * @see ServerRequestHeader
 */
public class ServerResponseHeader {
    private int status;
    private String msg;
    private String host;
    private int port;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSuccess() {
        return status == ResCode.SUCCESS;
    }
}
