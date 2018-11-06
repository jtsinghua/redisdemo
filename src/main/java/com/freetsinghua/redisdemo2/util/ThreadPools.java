package com.freetsinghua.redisdemo2.util;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author z.tsinghua
 * @date 2018/11/5
 */
public class ThreadPools {

    private static final ExecutorService EXECUTOR_SERVICE;

    static {
        int corePoolSize = 8;
        int maximumPoolSize = 8 * corePoolSize;

        EXECUTOR_SERVICE =
                new ThreadPoolExecutor(
                        corePoolSize,
                        maximumPoolSize,
                        6000,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(1024),
                        new DefaultThreadFactory("thread-pool"));
    }

    public static void execute(Runnable r) {
        EXECUTOR_SERVICE.execute(r);
    }

    public static void stop() {
        boolean shutdown = EXECUTOR_SERVICE.isShutdown();
        if (!shutdown) {
            EXECUTOR_SERVICE.shutdown();
        }
    }
}
