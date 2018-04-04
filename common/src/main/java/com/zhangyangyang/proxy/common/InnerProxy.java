package com.zhangyangyang.proxy.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhangyangyang on 2018/4/4.
 * 真正的代理
 */
public class InnerProxy implements Runnable {
    private InputStream outSocketInputStream;
    private OutputStream inSocketOutputStream;

    public InnerProxy(InputStream outSocketInputStream, OutputStream inSocketOutputStream) {
        this.outSocketInputStream = outSocketInputStream;
        this.inSocketOutputStream = inSocketOutputStream;
    }

    @Override
    public void run() {
        byte[] ret = new byte[2048];
        int inLen;

        while (true) {
            try {
                inLen = outSocketInputStream.read(ret);
                if (inLen == -1) {
                    break;
                }
                inSocketOutputStream.write(ret, 0, inLen);
                inSocketOutputStream.flush();
            } catch (IOException e) {
                // if connection reset or remote server close the connection
                break;
            }
        }
    }

}