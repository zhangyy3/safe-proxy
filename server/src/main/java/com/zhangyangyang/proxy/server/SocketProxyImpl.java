package com.zhangyangyang.proxy.server;

import com.zhangyangyang.proxy.EncryptException;
import com.zhangyangyang.proxy.common.ResCode;
import com.zhangyangyang.proxy.common.SocketProxy;
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

    private String name;
    private String password;

    public SocketProxyImpl(Socket inSocket) {
        super(inSocket);
    }

    @Override
    public void handle() throws IOException {
        // valid connect
        InputStream inSocketInputStream = inSocket.getInputStream();
        ServerRequestHeader header = buildRequestHeader(inSocketInputStream);
        ServerResponseHeader response = validRequest(header);
        if (response.isSuccess()) {
            name = header.getName();
            password = header.getPassword();
        }
        inSocket.getOutputStream().write(response.getStatus());
        inSocket.getOutputStream().flush();
        // todo real handle request info
    }


    private ServerResponseHeader validRequest(ServerRequestHeader header) {
        ServerResponseHeader response = new ServerResponseHeader();

        if (!valadPass(header.getName(), header.getPassword())) {
            response.setStatus(ResCode.INVALID_PASSWORD);
            response.setMsg("验证失败");
            return response;
        }

        try {
            String host = new String(AES.decrypt(password + name, header.getHost().getBytes()));
            int port = Integer.parseInt(new String(AES.decrypt(password + name, header.getPort().getBytes())));
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
    private boolean valadPass(String name, String password) {
        // TODO: 2018/4/2 valid password and name
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
