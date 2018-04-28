package com.zhangyangyang.proxy.common;

import com.zhangyangyang.proxy.proto.SafeBlock;

/**
 * Created by zhangyangyang on 2018/4/28.
 * 对SafeBlock的包装，增加其它业务处理信息
 */
public class SafeBlockWrapper {
    private SafeBlock safeBlock;
    private boolean success;
    private String cause;

    public static SafeBlockWrapper success(SafeBlock block) {
        SafeBlockWrapper blockWrapper = new SafeBlockWrapper();
        blockWrapper.safeBlock = block;
        blockWrapper.success = true;
        return blockWrapper;
    }

    public static SafeBlockWrapper failed(String cause) {
        SafeBlockWrapper blockWrapper = new SafeBlockWrapper();
        blockWrapper.cause = cause;
        return blockWrapper;
    }


    private SafeBlockWrapper() {

    }

    public SafeBlock getSafeBlock() {
        return safeBlock;
    }


    public boolean isSuccess() {
        return success;
    }


    public String getCause() {
        return cause;
    }


}
