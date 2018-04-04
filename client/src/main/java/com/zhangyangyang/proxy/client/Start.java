package com.zhangyangyang.proxy.client;

import com.zhangyangyang.proxy.common.SocketThreadPool;
import com.zhangyangyang.proxy.util.MD5;

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


    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
            LOGGER.error("缺少必要参数");
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))) {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                LOGGER.info("port:{}", socket.getPort());
                SocketThreadPool.submit(new SocketProxy(socket, args[1], MD5.md5(args[2])));
            }
        } catch (Exception e) {
            LOGGER.error("start server failed", e);
        } finally {
            SocketThreadPool.shutdown();
        }

    }
}
