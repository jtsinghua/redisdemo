package com.freetsinghua.redisdemo2.config;

import com.freetsinghua.redisdemo2.controller.MessageController;
import com.freetsinghua.redisdemo2.server.UndertowServer;
import com.freetsinghua.redisdemo2.server.util.UndertowServerUtils;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author z.tsinghua
 * @date 2018/10/31
 */
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(8);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setAllowCoreThreadTimeOut(false);
        taskExecutor.setMaxPoolSize(64);
        taskExecutor.setQueueCapacity(512);
        taskExecutor
                .setThreadFactory(new DefaultThreadFactory("spring-asyn-pool"));
        taskExecutor
                .setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.afterPropertiesSet();

        return taskExecutor;
    }
}
