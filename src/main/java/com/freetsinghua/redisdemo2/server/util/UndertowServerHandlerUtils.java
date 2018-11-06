package com.freetsinghua.redisdemo2.server.util;

import com.freetsinghua.redisdemo2.util.DefaultWebSocketConnectionCallback;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;

/**
 * @author z.tsinghua
 * @date 2018/11/5
 */
public class UndertowServerHandlerUtils {
    /** 设置接收路径 */
    private static final String PATH = "/myApp";

    public static HttpHandler addPrefixPath() {

        return Handlers.path()
                .addPrefixPath(PATH, Handlers.websocket(new DefaultWebSocketConnectionCallback()));
    }
}
