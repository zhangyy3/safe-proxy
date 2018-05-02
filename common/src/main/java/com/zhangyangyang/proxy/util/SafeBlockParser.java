package com.zhangyangyang.proxy.util;

import com.zhangyangyang.proxy.EncryptException;
import com.zhangyangyang.proxy.common.SafeBlockWrapper;
import com.zhangyangyang.proxy.proto.SafeBlock;
import com.zhangyangyang.proxy.proto.SafeBlockConst;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhangyangyang on 2018/4/28.
 * get a safeBlock from input stream
 */
public class SafeBlockParser {
    private static final int S_INIT = -1;
    private static final int S_HEAD = 0;

    private static final int CR = '\r';//13
    private static final int LF = '\n';//10

    /**
     * 创建数据块，用于传到stream 中
     *
     * @param password 加密的密钥
     * @param type     SafeBlock type
     * @param data     data
     * @return SafeBlock
     */
    public static SafeBlock create(String password, byte[] type, byte[] data) throws EncryptException {
        SafeBlock block = new SafeBlock();
        block.setHeader(SafeBlockConst.HEADER_1_BIN);
        block.setType(type);
        block.setData(AES.encrypt(password, data));
        return block;
    }

    public static String getType(byte[] type) {
        return new String(type);
    }

    public static byte[] getDate(byte[] data, String password) throws EncryptException {
        return AES.decrypt(password, data);
    }


    /**
     * 从流中读取数据组成safeBlock
     *
     * @param in 输入流
     * @return SafeBlockWrapper
     */
    public static SafeBlockWrapper parse(InputStream in) throws IOException {
        int state = S_INIT;
        int n;
        int step = 0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SafeBlock block = new SafeBlock();
        boolean success = false;
        String cause = "";
        while (true) {
            if (state == S_INIT) {
                state = S_HEAD;
                continue;
            }
            n = in.read();
            if (n == -1) {
                break;
            }
            if (n == CR) {
                n = in.read();
                if (n == LF) {
                    step++;
                    switch (step) {
                        // header
                        case 1:
                            block.setHeader(outputStream.toByteArray());
                            break;
                        // type
                        case 2:
                            block.setType(outputStream.toByteArray());
                            break;
                        // data
                        case 3:
                            block.setData(outputStream.toByteArray());
                            break;
                        default:
                            // do nothing
                            break;
                    }
                    outputStream.reset();
                    if (step == 3) {
                        success = true;
                        break;
                    }
                } else {
                    // protocol error
                    cause = "protocal error, step:" + step;
                    break;
                }
            } else {
                outputStream.write(n);
            }

        }

        if (success) {
            return SafeBlockWrapper.success(block);
        }

        return SafeBlockWrapper.failed(cause);
    }
}
