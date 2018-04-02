package com.zhangyangyang.proxy.client;

/**
 * Created by zhangyangyang on 2018/3/25.
 * parse request info to a Request object
 */
public class Request {
    //http or https
    private String scheme;
    private int port;
    private String host;
    // request method
    private String method;
    // request data
    private byte[] data;

    private String metaInfo;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(String metaInfo) {
        this.metaInfo = metaInfo;
    }
}

