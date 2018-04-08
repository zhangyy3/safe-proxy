package com.zhangyangyang.proxy.server;

import com.zhangyangyang.proxy.EncryptException;
import com.zhangyangyang.proxy.common.InnerProxy;
import com.zhangyangyang.proxy.common.ResCode;
import com.zhangyangyang.proxy.common.SocketProxy;
import com.zhangyangyang.proxy.common.SocketThreadPool;
import com.zhangyangyang.proxy.data.UserBean;
import com.zhangyangyang.proxy.data.UserDB;
import com.zhangyangyang.proxy.util.AES;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by zhangyangyang on 2018/3/28.
 *
 * @see SocketProxy
 */
public class SocketProxyImpl extends SocketProxy {

    private static final Logger LOGGER = LogManager.getLogger(SocketProxyImpl.class);

    private UserBean userBean;

    public SocketProxyImpl(Socket inSocket) {
        super(inSocket);
    }

    @Override
    public void handle() throws IOException {
        // valid connect
        InputStream inSocketInputStream = inSocket.getInputStream();
        ServerRequestHeader header = buildRequestHeader(inSocketInputStream);
        String userName = new String(header.getName());
        userBean = UserDB.getUserByName(userName);
        if (null == userBean) {
            LOGGER.warn("user:{} not exists", userName);
            inSocket.getOutputStream().write(ResCode.INVALID_PASSWORD);
            inSocket.getOutputStream().flush();
            inSocket.close();
            return;
        }
        ServerResponseHeader response = validRequest(header);
        inSocket.getOutputStream().write(response.getStatus());
        inSocket.getOutputStream().flush();
        if (!response.isSuccess()) {
            inSocket.close();
            return;
        }


        // listen client request
        outSocket = new Socket(response.getHost(), response.getPort());
        listenClientRequest(inSocketInputStream, header.getData());
        // bridge destination server
        SocketThreadPool.submit(new InnerProxy(outSocket.getInputStream(), inSocket.getOutputStream()));
        inSocket.close();
        outSocket.close();
    }

    private void listenClientRequest(InputStream in, byte[] initData) throws IOException {
        // 如果是http请求，第一次连接的时候，建立连接成功的同时也要处理实际请求的数据
        if (null != initData) {
            writeData2TargetServer(initData);
        }

        int n;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            while ((n = in.read()) != -1) {
                if (n == '\n') {
                    byte[] bytes = out.toByteArray();
                    LOGGER.info("msg:{}", new String(bytes));
                    writeData2TargetServer(bytes);
                    out.reset();
                } else {
                    out.write(n);
                }
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private void writeData2TargetServer(byte[] data) {
        try {
            byte[] bytes = AES.decrypt(new String(userBean.getPassword()), data);
            outSocket.getOutputStream().write(bytes);
            outSocket.getOutputStream().flush();
        } catch (EncryptException e) {
            LOGGER.warn("decrypt data failed", e);
        } catch (IOException e) {
            LOGGER.error("send data to target server failed, host:{}", outSocket.getInetAddress().getHostAddress(), e);
        }
    }


    private ServerResponseHeader validRequest(ServerRequestHeader header) {

        ServerResponseHeader response = new ServerResponseHeader();

        if (!validPass(new String(header.getName()), new String(header.getPassword()))) {
            response.setStatus(ResCode.INVALID_PASSWORD);
            response.setMsg("验证失败");
            return response;
        }

        try {

            String secret = new String(userBean.getPassword());
            String host = new String(AES.decrypt(secret, header.getHost()));
            int port = Integer.parseInt(new String(AES.decrypt(secret, header.getPort())));
            response.setPort(port);
            response.setHost(host);
            response.setStatus(ResCode.SUCCESS);
        } catch (EncryptException e) {
            LOGGER.error("解密数据失败", e);
            response.setStatus(ResCode.DATA_ERROR);
            response.setMsg("解密数据失败");
        }


        return response;
    }

    // now not valid the pass , just return ture
    private boolean validPass(String name, String password) {

        if (!userBean.validPassword(password)) {
            LOGGER.info("user:{}, 传入的密码:{}不正确", name, password);
            return false;
        }
        if (!userBean.validExpire()) {
            LOGGER.info("user:{}, 已过期", name);
            return false;
        }
        return true;
    }

    private ServerRequestHeader buildRequestHeader(InputStream in) throws IOException {
        ServerRequestHeader header = new ServerRequestHeader();
        int count = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 第一次请求有多少行
        int blocks = 5;
        byte[][] data = new byte[blocks][];
        while (count < blocks) {
            byte b = (byte) in.read();
            if (b == '\n') {
                data[count] = out.toByteArray();
                count++;
                out.reset();
            } else {
                out.write(b);
            }
        }
        header.setName(data[0]);
        header.setPassword(data[1]);
        header.setHost(data[2]);
        header.setPort(data[3]);
        header.setData(data[4]);
        return header;
    }
}
