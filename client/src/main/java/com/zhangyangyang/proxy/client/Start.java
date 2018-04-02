package com.zhangyangyang.proxy.client;

import com.zhangyangyang.proxy.common.SocketThreadPool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zhangyangyang on 2018/3/23.
 * start the proxy
 */
public class Start {

    private static final Logger LOGGER = LogManager.getLogger(Start.class);

    private static boolean run = true;

    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {

            while (run) {
                Socket socket = serverSocket.accept();
                LOGGER.info("port:{}", socket.getPort());
                SocketThreadPool.submit(new SocketProxy(socket));
            }
        } catch (Exception e) {
            LOGGER.error("start server failed", e);
        } finally {
            SocketThreadPool.shutdown();
        }

    }
}
