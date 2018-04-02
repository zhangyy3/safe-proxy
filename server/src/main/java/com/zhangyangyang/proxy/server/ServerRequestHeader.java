package com.zhangyangyang.proxy.server;

/**
 * Created by zhangyangyang on 2018/3/28.
 * first connect info
 */
public class ServerRequestHeader {
    private String name;
    private String password;
    private String host;
    private String port;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
