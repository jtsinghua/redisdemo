package com.freetsinghua.redisdemo2.model;

import io.undertow.websockets.core.WebSocketChannel;

import java.util.Objects;

/**
 * @author z.tsinghua
 * @date 2018/10/29
 */
public class Sender {
    private String userId;
    private WebSocketChannel webSocketChannel;

    public Sender() {
    }

    public Sender(String userId, WebSocketChannel webSocketChannel) {
        this.userId = userId;
        this.webSocketChannel = webSocketChannel;
    }

    public String getUserId() {
        return userId;
    }

    public WebSocketChannel getWebSocketChannel() {
        return webSocketChannel;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setWebSocketChannel(WebSocketChannel webSocketChannel) {
        this.webSocketChannel = webSocketChannel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sender sender = (Sender) o;
        return Objects.equals(userId, sender.userId) &&
                       Objects.equals(webSocketChannel, sender.webSocketChannel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, webSocketChannel);
    }
}
