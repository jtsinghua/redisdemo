package com.freetsinghua.redisdemo2.util;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

/**
 * @author z.tsinghua
 * @date 2018/11/1
 */
public class DefaultWebSocketConnectionCallback implements WebSocketConnectionCallback {

    @Override
    public void onConnect(
            WebSocketHttpExchange webSocketHttpExchange, WebSocketChannel webSocketChannel) {
        webSocketChannel.getReceiveSetter().set(new DefaultReceiveListener());
        webSocketChannel.resumeReceives();
    }
}
