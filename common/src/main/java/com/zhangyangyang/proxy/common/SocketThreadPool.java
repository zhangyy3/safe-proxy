package com.zhangyangyang.proxy.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhangyangyang on 2018/3/27.
 * provide a thread to run socket connect
 */
public class SocketThreadPool {
    private static final ExecutorService service = Executors.newFixedThreadPool(256);

    public static void submit(Runnable run) {
        service.execute(run);
    }

    public static void shutdown() {
        service.shutdown();
    }
}
