package com.zhangyangyang.proxy.client.test;


import org.junit.Test;

import java.net.Socket;

/**
 * Created by zhangyangyang on 2018/3/26.
 *  test http  connect method
 */
public class ConnectTest {

    @Test
    public void connectTest() throws Exception {
        String str = "CONNECT timgsa.baidu.com:443 HTTP/1.1\r\n" +
                "cache-control: no-cache\r\n" +
                "User-Agent: PostmanRuntime/7.1.1\r\n" +
                "Accept: */*\r\n" +
                "host: timgsa.baidu.com:443\r\n" +
//                "Connection: close\r\n" +
//                "Connection: keep-alive\r\n" +
                "\r\n" +
                "\r\n";
        System.out.println(str);
        Socket socket = new Socket("timgsa.baidu.com",443);
        socket.setKeepAlive(false);
        socket.getOutputStream().write(str.getBytes("utf8"));
        byte[] ret = new byte[2048 * 65];
        int len = socket.getInputStream().read(ret);
        System.out.println(new String (ret, 0, len));
    }
}
