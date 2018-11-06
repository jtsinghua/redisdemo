package com.freetsinghua.redisdemo2.server.util;

import com.freetsinghua.redisdemo2.server.UndertowServer;
import io.undertow.websockets.core.WebSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * 通过工具类来操作服务器
 *
 * @author z.tsinghua
 * @date 2018/11/5
 */
public class UndertowServerUtils {
    private static final String TAG = UndertowServerUtils.class.getName() + ":";
    private static final Logger LOGGER = LoggerFactory.getLogger(UndertowServerUtils.class);

    /**
     * 启动服务器
     *
     * @deprecated 通过UndertowServer类注册@Component，来启动服务器
     */
    @Deprecated
    public static void start() {
        try {

            UndertowServer.getUndertow().start();
        } catch (Exception e) {
            // 若是启动失败
            LOGGER.error("{}启动服务器失败：【{}】", TAG, e.getMessage(), e);
        }
    }

    /**
     * 关闭服务器
     *
     * @deprecated 服务器直到APP关闭，才会关闭
     */
    @Deprecated
    public static void stop() {
        try {
            UndertowServer.getUndertow().stop();
        } catch (Exception e) {
            // 若是关闭失败
            LOGGER.error("{}关闭服务器错误：【{}】", TAG, e.getMessage(), e);
        }
    }

    /**
     * 获取注册表
     *
     * @deprecated 使用getWebSocketChannel方法，直接获取连接
     * @return 返回注册表
     */
    @Deprecated
    public static Map<String, WebSocketChannel> getChannelMap() {
        return UndertowServer.getChannelMap();
    }

    /**
     * 根据id从注册表中获取对应的连接
     *
     * @param userId 用户id
     * @return 返回连接，如果不存在，返回null
     */
    @Nullable
    public static WebSocketChannel getWebSocketChannel(String userId) {
        return UndertowServer.getChannelMap().get(userId);
    }

    /**
     * 使用undertow的线程池,使用ThreadPools中的execute方法代替
     *
     * @param r Runnable实例,表示要执行的动作
     */
    @Deprecated
    public static void execute(Runnable r) {
        UndertowServer.getUndertow().getWorker().execute(r);
    }
}
