package com.zhangyangyang.proxy.client;


import com.zhangyangyang.proxy.EncryptException;
import com.zhangyangyang.proxy.common.InnerProxy;
import com.zhangyangyang.proxy.common.ResCode;
import com.zhangyangyang.proxy.common.SocketThreadPool;
import com.zhangyangyang.proxy.util.AES;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by zhangyangyang on 2018/3/25.
 * proxy
 */
public class ClientSocketProxy implements Runnable {

    // 换行\n
    private static final byte[] LF = "\n".getBytes();

    private static final Logger LOGGER = LogManager.getLogger(ClientSocketProxy.class);
    private Socket inSocket;
    private Socket outSocket;
    private Request request;
    private String name;
    private String password;

    private static final String AUTHORED = "HTTP/1.1 200 Connection established\r\n\r\n";
    private static final String CONNECT_FAILED = "HTTP/1.1 401 Connection failed\r\n\r\n";


    ClientSocketProxy(Socket inSocket, String name, String passwd) {
        this.inSocket = inSocket;
        this.name = name;
        this.password = passwd;
    }

    private void handle() {

        try {
            request = getRequest();
            if (null == request) {
                LOGGER.error("get request failed");
                return;
            }
            LOGGER.info("request data:{}", new String(request.getData()));
            outSocket = new Socket("127.0.0.1", 8889);
            outSocket.setKeepAlive(true);
            // set timeout 10s
            outSocket.setSoTimeout(100000);

            if (request.getMethod().equals(RM.CONNECT)) {
                inSocket.getOutputStream().write(AUTHORED.getBytes());
                inSocket.getOutputStream().flush();
            } else {
                boolean connected = connectProxyServer();
                if (!connected) {
                    inSocket.getOutputStream().write(CONNECT_FAILED.getBytes());
                    inSocket.getOutputStream().flush();
                    inSocket.close();
                    return;
                }
            }

            InputStream inSocketInputStream = inSocket.getInputStream();
            SocketThreadPool.submit(new InnerProxy(outSocket.getInputStream(), inSocket.getOutputStream()));

            handleClientRequest(inSocketInputStream);
        } catch (Exception e) {
            LOGGER.error("connection exception, request:{}", request == null ? "" : request.getMetaInfo(), e);
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
        while (!inSocket.isClosed()) {
            try {
                len = inSocketInputStream.read(readBytes);
                if (len == -1) {
                    break;
                }
                outSocket.getOutputStream().write(AES.encrypt(password, Arrays.copyOfRange(readBytes, 0, len)));
                outSocket.getOutputStream().write(LF);
                outSocket.getOutputStream().flush();
            } catch (IOException e) {
                // connection close or reset
                LOGGER.warn("browser connect failed", e);
                break;
            } catch (EncryptException e) {
                // encrypt failed
                LOGGER.warn("encrypt failed", e);
                break;
            }
        }
    }


    private Request getRequest() throws IOException {
        InputStream in = inSocket.getInputStream();
        byte[] buf = new byte[2 << 14];
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

    /**
     * 同步连接proxy server, 如果成功，则建立连接，否则返回失败信息
     */
    private boolean connectProxyServer() {
        // the proxy server host and port
        try  {

            OutputStream out = outSocket.getOutputStream();
            out.write(name.getBytes());
            out.write(LF);
            out.write(password.getBytes());
            out.write(LF);
            out.write(AES.encrypt(password, request.getHost().getBytes()));
            out.write(LF);
            out.write(AES.encrypt(password, (request.getPort() + "").getBytes()));
            out.write(LF);
            out.write(AES.encrypt(password, request.getData()));
            out.write(LF);
            out.flush();
            //out.close();

            InputStream in = outSocket.getInputStream();
            int n = in.read();
            if (n == ResCode.SUCCESS) {
                return true;
            }
            LOGGER.error("connect failed, response code:{}", n);


        } catch (UnknownHostException e) {
            LOGGER.error("UnknownHost:{}", request.getHost());
        } catch (IOException e) {
            LOGGER.error("connect to proxy server failed", e);
        } catch (EncryptException e) {
            LOGGER.error("encrypt failed", e);
        }
        return false;
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
