package com.zhangyangyang.proxy.proto;

/**
 * Created by zhangyangyang on 2018/4/28.
 * 协议包
 * |--------------------------------------------|
 * |safeBlock version\r\n                       |
 * |type\r\n                                    |
 * |data\r\n                                    |
 * |\r\n                                        |
 * |--------------------------------------------|
 */
public class SafeBlock {
    private byte[] header;
    private byte[] type;
    private byte[] data;

    public byte[] getHeader() {
        return header;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public byte[] getType() {
        return type;
    }

    public void setType(byte[] type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
