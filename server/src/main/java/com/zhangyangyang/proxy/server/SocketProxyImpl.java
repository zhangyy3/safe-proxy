package com.zhangyangyang.proxy.server;

import com.zhangyangyang.proxy.common.SocketProxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by zhangyangyang on 2018/3/28.
 *
 * @see SocketProxy
 */
public class SocketProxyImpl extends SocketProxy {

    private String name;
    private String password;
    private String secret;

    public SocketProxyImpl(Socket inSocket) {
        super(inSocket);
    }

    @Override
    public void handle() throws IOException {
        InputStream inSocketInputStream = inSocket.getInputStream();
        ServerRequestHeader header = buildRequestHeader(inSocketInputStream);
        ServerResponseHeader responseHeader = validRequest(header);
        // todo

    }

    private ServerResponseHeader validRequest(ServerRequestHeader header) {
        ServerResponseHeader response = new ServerResponseHeader();
        // TODO: 2018/4/2
        return response;
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
