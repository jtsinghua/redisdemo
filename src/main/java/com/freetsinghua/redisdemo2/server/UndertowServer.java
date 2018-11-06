package com.freetsinghua.redisdemo2.server;

import com.freetsinghua.redisdemo2.server.util.UndertowServerHandlerUtils;
import com.freetsinghua.redisdemo2.util.DefaultReceiveListener;
import io.undertow.Undertow;
import io.undertow.connector.ByteBufferPool;
import io.undertow.server.XnioByteBufferPool;
import io.undertow.websockets.core.WebSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xnio.ByteBufferSlicePool;
import org.xnio.Pool;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;

/**
 * undertow服务类,通过注册到spring容器中，启动服务器
 *
 * @author z.tsinghua
 * @date 2018/10/29
 */
@Component
@Slf4j
public class UndertowServer {

    private static final int IO_THREADS = 8;
    private static final int WORKER_THREADS = IO_THREADS * 8;

    private static final Undertow UNDERTOW;
    /** 监听IP */
    private static final String HOST = "localhost";
    /** 监听端口 */
    private static final int PORT = 8083;

    static {
        // 初始化服务器
        UNDERTOW = init();
        // 启动服务器
        start();
    }

    private static Undertow init() {
        try {

            Undertow.Builder builder = Undertow.builder();
            Pool<ByteBuffer> bufferPool =
                    new ByteBufferSlicePool(ByteBuffer::allocate, 1024, 1024, 10);
            ByteBufferPool byteBufferPool = new XnioByteBufferPool(bufferPool);

            return builder.addHttpListener(PORT, HOST)
                    .setIoThreads(IO_THREADS)
                    .setWorkerThreads(WORKER_THREADS)
                    .setByteBufferPool(byteBufferPool)
                    .setHandler(UndertowServerHandlerUtils.addPrefixPath())
                    .build();
        } catch (Exception e) {
            log.error("构造Undertow对象时失败：【{}】", e.getMessage(), e);
            return null;
        }
    }

    /** 启动服务器 */
    private static void start() {

        try {
            Objects.requireNonNull(UNDERTOW).start();
            log.info("服务器启动！监听【{}】的端口【{}】", HOST, PORT);
        } catch (Exception e) {
            log.error("服务器启动失败：【{}】", e.getMessage(), e);
        }
    }

    /**
     * 获取用户注册表
     *
     * @return 返回用户注册表
     */
    public static Map<String, WebSocketChannel> getChannelMap() {
        return DefaultReceiveListener.CHANNEL_MAP;
    }

    public static Undertow getUndertow() {
        return UndertowServer.UNDERTOW;
    }
}
