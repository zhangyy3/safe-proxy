package com.zhangyangyang.proxy.server;

/**
 * Created by zhangyangyang on 2018/3/28.
 * first connect info
 */
public class ServerRequestHeader {

    private byte[] name;
    private byte[] password;
    private byte[] host;
    private byte[] port;
    private byte[] data;

    public byte[] getName() {
        return name;
    }

    public void setName(byte[] name) {
        this.name = name;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public byte[] getHost() {
        return host;
    }

    public void setHost(byte[] host) {
        this.host = host;
    }

    public byte[] getPort() {
        return port;
    }

    public void setPort(byte[] port) {
        this.port = port;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
