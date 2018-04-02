package com.zhangyangyang.proxy.server;

import com.zhangyangyang.proxy.common.SocketThreadPool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zhangyangyang on 2018/3/28.
 * proxy the server socket and dispatch socket connection
 */
public class ServerSocketProxy {

    private static final Logger LOGGER = LogManager.getLogger(ServerSocketProxy.class);

    private ServerSocket serverSocket;
    private int port;

    public ServerSocketProxy(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
    }

    public void listen() throws IOException {
        LOGGER.info("server start, listen port:{} ", port);
        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            SocketThreadPool.submit(new SocketProxyImpl(socket));
        }
        SocketThreadPool.shutdown();
    }

    public void destroy() throws IOException {
        serverSocket.close();
    }
}
