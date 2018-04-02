package com.zhangyangyang.proxy.server;

/**
 * Created by zhangyangyang on 2018/3/28.
 */
public class ServerResponseHeader {
    private int status;
    private String msg;

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
}
