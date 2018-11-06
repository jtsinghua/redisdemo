package com.freetsinghua.redisdemo2.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时器，用于将数据从Redis刷新到MySQL中，随着spring一同启动
 *
 * @author z.tsinghua
 * @date 2018/11/5
 */
@Component
@Slf4j
public class RefreshRedisTimer {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE;

    private static final long CRITICAL_VALUE = 50000L;

    static {
        SCHEDULED_EXECUTOR_SERVICE =
                new ScheduledThreadPoolExecutor(
                        1,
                        new BasicThreadFactory.Builder()
                                .namingPattern("example-schedule-pool-%d")
                                .daemon(true)
                                .build());
        // 一分钟
        long initialDelay = 1000 * 60;
        // 五分钟
        long period = 1000 * 60 * 5;

        SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(
                () -> {
                    log.info("定时器开始工作!");
                    long size = RedisUtils.getRedisHashUtils().size();
                    log.info("size = 【{}】", size);
                    if (size > CRITICAL_VALUE) {
                        CacheUtils.redisToMySql(null);
                    }
                },
                initialDelay,
                period,
                TimeUnit.MILLISECONDS);

        log.info("定时器已经加载【用于刷新Redis】，将在一分钟后启动，每个5分钟检测一次!");
    }
}
