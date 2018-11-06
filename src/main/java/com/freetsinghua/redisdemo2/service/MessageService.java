package com.freetsinghua.redisdemo2.service;

import com.freetsinghua.redisdemo2.constant.Constants;
import com.freetsinghua.redisdemo2.model.Message;
import com.freetsinghua.redisdemo2.server.util.UndertowServerUtils;
import com.freetsinghua.redisdemo2.util.RedisHashUtils;
import com.freetsinghua.redisdemo2.util.RedisUtils;
import com.freetsinghua.redisdemo2.util.StringUtils;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author z.tsinghua
 * @date 2018/10/30
 */
@Service
@Slf4j
public class MessageService {
    private static final RedisHashUtils REDIS_HASH_UTILS = RedisUtils.getRedisHashUtils();

    public String sendMessage(Message message) {
        String result;

        WebSocketChannel webSocketChannel =
                UndertowServerUtils.getWebSocketChannel(message.getDest());

        if (webSocketChannel != null && webSocketChannel.isOpen()) {
            WebSockets.sendText(message.getMsg(), webSocketChannel, null);
            log.info("发送消息成功:从[{}]到[{}]", message.getSrc(), message.getDest());
            result = Constants.RETURN_OK.getValue();
        } else {
            // 如果用户不在线
            log.info("用户不在线，先存在Redis中");

            // 组装消息实体
            message.setId(StringUtils.uuid());
            message.setRemark("用户不在线");

            // 刷新Redis，用定时器
            REDIS_HASH_UTILS.put(StringUtils.generateKey(message.getDest()), message);

            result = Constants.RETURN_ERROR_NOT_ONLINE.getValue();
        }

        return result;
    }
}
