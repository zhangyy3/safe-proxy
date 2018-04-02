package com.zhangyangyang.proxy.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by zhangyangyang on 2018/3/28.
 * start the socket server
 */
public class ServerStart {
    private static final Logger LOGGER = LogManager.getLogger(ServerStart.class);
    public static void main(String[] args) throws Exception {
        int port = 8889;
        try {
            new ServerSocketProxy(port).listen();
        } catch (Exception e) {
            LOGGER.error("start server failed", e);
        }
    }
}
