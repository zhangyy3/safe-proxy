package com.zhangyangyang.proxy.util;

import com.zhangyangyang.proxy.common.SafeBlockWrapper;
import com.zhangyangyang.proxy.proto.SafeBlock;

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
