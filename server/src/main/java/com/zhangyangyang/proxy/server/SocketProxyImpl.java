package com.zhangyangyang.proxy.server;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
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
        ServerResponseHeader response = validRequest(header);
        inSocket.getOutputStream().write(response.getStatus());
        inSocket.getOutputStream().flush();
        if (!response.isSuccess()) {
            userBean = UserDB.getUserByName(header.getName());
            inSocket.close();
            return;
        }

        outSocket = new Socket(response.getHost(), response.getPort());
        // listen client request
        listenClientRequest(inSocketInputStream);
        // bridge destination server
        SocketThreadPool.submit(new InnerProxy(outSocket.getInputStream(), inSocket.getOutputStream()));
        inSocket.close();
        outSocket.close();
    }

    private void listenClientRequest(InputStream in) throws IOException {
        int n;
        try (ByteOutputStream out = new ByteOutputStream()) {
            while ((n = in.read()) != -1) {
                if (n == '\n') {
                    byte[] bytes = out.getBytes();
                    try {
                        // decrypt bytes
                        bytes = AES.decrypt(new String(userBean.getPassword()), bytes);
                        outSocket.getOutputStream().write(bytes);
                        outSocket.getOutputStream().flush();
                    } catch (EncryptException e) {
                        LOGGER.error("decrypt data block failed, continue...", e);
                    }
                } else {
                    out.write(n);
                }
            }
        }
    }


    private ServerResponseHeader validRequest(ServerRequestHeader header) {

        ServerResponseHeader response = new ServerResponseHeader();

        if (!validPass(header.getName(), header.getPassword())) {
            response.setStatus(ResCode.INVALID_PASSWORD);
            response.setMsg("验证失败");
            return response;
        }

        try {
            String secret = userBean.getName() + new String(userBean.getPassword());
            String host = new String(AES.decrypt(secret, header.getHost().getBytes()));
            int port = Integer.parseInt(new String(AES.decrypt(secret, header.getPort().getBytes())));
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
        UserBean bean = UserDB.getUserByName(name);
        if (null == bean) {
            LOGGER.info("user:{} 不存在", name);
            return false;
        }
        if (!bean.validPassword(password)) {
            LOGGER.info("user:{}, 传入的密码:{}不正确", name, password);
            return false;
        }
        if (!bean.validExpire()) {
            LOGGER.info("user:{}, 已过期", name);
            return false;
        }
        return true;
    }

    private ServerRequestHeader buildRequestHeader(InputStream in) throws IOException {
        ServerRequestHeader header = new ServerRequestHeader();
        int count = 0;
        StringBuilder sb = new StringBuilder();
        while (count < 4) {
            char c = (char) in.read();
            sb.append(c);
            if (c == '\n') {
                count++;
            }
        }
        String[] raw = sb.toString().split("\n");
        header.setName(raw[0]);
        header.setPassword(raw[1]);
        header.setHost(raw[2]);
        header.setPort(raw[3]);
        return header;
    }
}
