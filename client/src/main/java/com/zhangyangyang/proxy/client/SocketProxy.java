package com.zhangyangyang.proxy.client;


import com.zhangyangyang.proxy.common.SocketThreadPool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by zhangyangyang on 2018/3/25.
 * proxy
 */
public class SocketProxy implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(SocketProxy.class);
    private Socket inSocket;
    private Socket outSocket;
    private Request request;

    private static final String AUTHORED = "HTTP/1.1 200 Connection established\r\n\r\n";

    private transient boolean run = true;

    SocketProxy(Socket inSocket) {
        this.inSocket = inSocket;
    }

    private void handle() {

        try {
            request = getRequest();
            if (null == request) {
                return;
            }
            LOGGER.info("request data:{}", new String(request.getData()));
            outSocket = new Socket(request.getHost(), request.getPort());
            outSocket.setKeepAlive(true);
            if (request.getMethod().equals(RM.CONNECT)) {
                inSocket.getOutputStream().write(AUTHORED.getBytes());
                inSocket.getOutputStream().flush();
            } else {
                outSocket.getOutputStream().write(request.getData());
                outSocket.getOutputStream().flush();
            }
            InputStream inSocketInputStream = inSocket.getInputStream();

            SocketThreadPool.submit(new InnerProxy(outSocket.getInputStream(), inSocket.getOutputStream()));

            handleClientRequest(inSocketInputStream);
        } catch (Exception e) {
            LOGGER.error("connection exception, request:{}", request.getMetaInfo(), e);
            run = false;
        }
    }

    /**
     * 处理客户端的请求，读取数据，响应数据
     *
     * @param inSocketInputStream inSocket.getInputStream()
     */
    private void handleClientRequest(InputStream inSocketInputStream) {
        byte[] readBytes = new byte[4096];
        int len;
        while (run && !inSocket.isClosed()) {
            try {
                len = inSocketInputStream.read(readBytes);
                if (len == -1) {
                    run = false;
                    break;
                }
                outSocket.getOutputStream().write(readBytes, 0, len);
                outSocket.getOutputStream().flush();
            } catch (IOException e) {
                // connection close or reset
                run = false;
            }
        }
    }


    private Request getRequest() throws IOException {
        InputStream in = inSocket.getInputStream();
        byte[] buf = new byte[1024 * 1024];
        int length = in.read(buf);
        if (length < 0) {
            LOGGER.info("no data for the request");
            return null;
        }
        String rawReq = new String(buf, 0, length);
        String[] splits = rawReq.split("\r\n");
        String hostAndPost = splits[0];
        String[] value = hostAndPost.split(" ");
        Request temp = new Request();
        temp.setMetaInfo(hostAndPost);
        String method = value[0];
        temp.setMethod(method);
        if (RM.CONNECT.equals(method)) {
            temp.setScheme("https");
            temp.setPort(443);
            temp.setHost(value[1].split(":")[0]);
        } else {
            URL url = new URL(value[1]);
            temp.setHost(url.getHost());
            temp.setScheme(url.getProtocol());
            int port = url.getPort();
            if (port < 0) {
                if ("https".equals(temp.getScheme())) {
                    port = 443;
                } else {
                    port = 80;
                }
            }
            temp.setPort(port);
        }

        temp.setData(Arrays.copyOfRange(buf, 0, length));
        return temp;
    }

    private class InnerProxy implements Runnable {
        private InputStream outSocketInputStream;
        private OutputStream inSocketOutputStream;

        InnerProxy(InputStream outSocketInputStream, OutputStream inSocketOutputStream) {
            this.outSocketInputStream = outSocketInputStream;
            this.inSocketOutputStream = inSocketOutputStream;
        }

        @Override
        public void run() {
            byte[] ret = new byte[2048];
            int inLen;

            while (run) {
                try {
                    inLen = outSocketInputStream.read(ret);
                    if (inLen == -1) {
                        run = false;
                        break;
                    }
                    inSocketOutputStream.write(ret, 0, inLen);
                    inSocketOutputStream.flush();
                } catch (IOException e) {
                    // if connection reset or remote server close the connection
                    run = false;
                    break;
                }
            }
            releaseConnection();

        }

        private void releaseConnection() {
            try {
                if (null != request)
                    LOGGER.info("release connection:{}", request.getMetaInfo());

                if (!inSocket.isClosed()) {
                    inSocket.close();
                }

                if (null != outSocket && !outSocket.isClosed()) {
                    outSocket.close();
                }

            } catch (Exception e) {
                LOGGER.error("release connection failed", e);
            }
        }
    }


    @Override
    public void run() {
        try {
            handle();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
