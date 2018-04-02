package com.zhangyangyang.proxy.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by zhangyangyang on 2018/3/28.
 * handle one socket throgh a thread
 */
public abstract class SocketProxy implements Runnable {


    private static final Logger LOGGER = LogManager.getLogger(SocketProxy.class);

    /**
     * 用来接收客户端的请求
     */
    protected Socket inSocket;
    /**
     * 负责和服务端连接
     */
    protected Socket outSocket;

    public SocketProxy(Socket inSocket) {
        this.inSocket = inSocket;
    }

    @Override
    public void run() {
        try {
            handle();
        } catch (IOException e) {
            LOGGER.error("handle socket failed", e);
        }
    }

    public abstract void handle() throws IOException;
}
