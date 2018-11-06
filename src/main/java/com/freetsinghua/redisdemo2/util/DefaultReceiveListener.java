package com.freetsinghua.redisdemo2.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.freetsinghua.redisdemo2.model.Message;
import io.undertow.websockets.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author z.tsinghua
 * @date 2018/11/1
 */
public class DefaultReceiveListener extends AbstractReceiveListener {
    /** 保存已注册用户以及连接 */
    public static final Map<String, WebSocketChannel> CHANNEL_MAP = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultReceiveListener.class);
    /** 表示消息的类型，注册 */
    private static final String MESSAGE_TYPE_CONNECT = "00";
    /** 表示消息的类型，发送消息 */
    private static final String MESSAGE_TYPE_MESSAGE = "01";
    /** Redis中保存的最多消息数量，超过这个临界值，将刷入MySQL中 */
    private static final int CRITICAL_VALUE = 1000000;

    /** 操作redis的工具 */
    private static final RedisHashUtils REDIS_HASH_UTILS = RedisUtils.getRedisHashUtils();
    /** 锁 */
    private static final Lock LOCK = new Lock();

    /** 计数，记录有多少数据已经存入redis中 */
    private static volatile long count = 0L;

    private String userId = null;

    /**
     * 接收文本消息 定制消息格式，type字段必须有。 若是首次建立连接，则type：connect 若是发送消息，则type：msg
     *
     * @param channel 当前连接
     * @param message 消息实体
     */
    @Override
    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
        String data = message.getData();

        JSONObject jsonObject = JSON.parseObject(data);
        String type = jsonObject.getString("type");

        if (StringUtils.isEmpty(type)) {
            LOGGER.error("用户注册失败：【字段type的值不能为空或null】");
            return;
        }

        // 首次建立连接,处理注册
        if (MESSAGE_TYPE_CONNECT.equals(type)) {
            register(channel, jsonObject);
        }

        // 发送消息
        if (MESSAGE_TYPE_MESSAGE.equals(type)) {
            sendMessage(jsonObject);
        }
    }

    /**
     * 处理初次登陆，也就是注册
     *
     * @param channel 当前连接
     * @param jsonObject 接收的数据
     */
    private void register(WebSocketChannel channel, JSONObject jsonObject) {

        userId = jsonObject.getString("userId");

        if (StringUtils.isEmpty(userId)) {
            LOGGER.error("用户注册失败：【userID字段值不能为null】");
            return;
        }

        // 每次登陆，都查看一下有没有僵尸链接
        checkChannels();

        // 检测是否已经有相同的userID登陆
        syncLogin();

        // 写入Redis中
        CHANNEL_MAP.put(userId, channel);

        LOGGER.info("已经建立连接[{}]", userId);

        // 异步处理，处理Redis中的数据
        ThreadPools.execute(
                () -> {
                    handleRedis(channel);
                });

        // 异步处理，处理MySQL中的数据
        ThreadPools.execute(
                () -> {
                    handleMySql(channel);
                });
    }

    /** 处理同一个用户多次登陆，同时在线的问题 */
    private void syncLogin() {

        WebSocketChannel value = CHANNEL_MAP.get(userId);
        // 之前的处理僵尸连接，可以保证value不为null
        if (value != null && value.isOpen()) {
            try {
                value.close();
            } catch (IOException e) {
                LOGGER.error("关闭连接失败,但已经从map中移除：【{}】", e.getMessage());
            }
        }
        CHANNEL_MAP.remove(userId);

        LOGGER.info("检测异地登陆处理完毕.");
    }

    /** 查看是否保存了将是连接 */
    private void checkChannels() {

        List<String> keysWillBeDeleted = new ArrayList<>();

        for (Map.Entry<String, WebSocketChannel> entry : CHANNEL_MAP.entrySet()) {

            WebSocketChannel value = entry.getValue();
            if (value == null || !value.isOpen()) {
                keysWillBeDeleted.add(entry.getKey());
            }
        }

        // 清除僵尸连接
        for (String key : keysWillBeDeleted) {
            CHANNEL_MAP.remove(key);
        }

        LOGGER.info("僵尸连接处理完毕.");
    }

    /**
     * 发送消息
     *
     * @param jsonObject 消息格式的json字符串
     */
    private void sendMessage(JSONObject jsonObject) {
        String from = jsonObject.getString("src");
        String to = jsonObject.getString("dest");
        String msg = jsonObject.getString("msg");

        Message message = new Message(from, to, msg);
        message.setId(StringUtils.uuid());
        message.setRemark(from + " to " + to);

        LOGGER.info("收到来自消息：由[{}] 发送到 [{}],内容是[{}]", from, to, msg);

        // 如果对方在线，发送
        WebSocketChannel wc = CHANNEL_MAP.get(to);
        if (wc != null && wc.isOpen()) {
            WebSockets.sendText(msg, wc, null);
            LOGGER.info("对方在线，消息发送成功.");
            return;
        }

        // 如果不在线
        // 先写入Redis中，在掉线的时候，放到MySQL
        LOGGER.info("对方不在线，先将消息放入缓存redis中.");

        String key;

        // 由于需要根据目标用户ID获取
        key = StringUtils.generateKey(to);

        REDIS_HASH_UTILS.put(key, message);

        // volatile关键字不保证原子性
        synchronized (LOCK) {
            ++count;
        }

        // 当Redis中为发送的消息达到一定数量，刷回MySQL数据库中
        if (count >= CRITICAL_VALUE) {
            refresh(message);
            // 归零，重新计数
            count = 0;
        }
    }

    private void refresh(Message m) {
        LOGGER.info("消息已经达到[{}]条，开始刷入MySQL数据库中", CRITICAL_VALUE);

        CacheUtils.redisToMySql(m.getDest());
    }

    private void handleRedis(WebSocketChannel channel) {
        // 用户上线，先在Redis中查找 统一保存在哈希表中messages key为userId#System.currentMillis()
        Map<String, Message> messages = REDIS_HASH_UTILS.entries(userId);

        for (Map.Entry<String, Message> entry : messages.entrySet()) {
            Message value = entry.getValue();

            if (StringUtils.isEmpty(value.getMsg())) {
                continue;
            }

            WebSockets.sendText(value.getMsg() + "#来自Redis", channel, null);
            REDIS_HASH_UTILS.delete(entry.getKey());
            LOGGER.info("从Redis中删除键【{}】成功.", entry.getKey());
        }
    }

    private void handleMySql(WebSocketChannel channel) {
        // 从数据库中读取
        String sql;

        while (true) {
            // 限制每次最多取1000条数据
            sql = "SELECT id, msg FROM messages WHERE dest = ? limit 1000";
            List<Map<String, Object>> maps =
                    BeanManager.getJdbcTemplate().queryForList(sql, userId);

            if (maps == null || maps.size() == 0) {
                break;
            }

            List<String> ids = new ArrayList<>();

            for (Map<String, Object> map : maps) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if ("id".equals(entry.getKey())) {
                        ids.add((String) entry.getValue());
                    } else if ("msg".equals(entry.getKey())) {
                        WebSockets.sendText(entry.getValue() + " #来自MySQL", channel, null);
                    }
                }
            }

            sql = "DELETE FROM messages WHERE id = ?";
            for (String id : ids) {
                try {
                    BeanManager.getJdbcTemplate().update(sql, id);
                    LOGGER.info("从MySQL中删除成功.");
                } catch (Exception e) {
                    LOGGER.error("删除失败[{}]", e.getMessage());
                }
            }
        }
    }

    @Override
    protected void onFullBinaryMessage(WebSocketChannel channel, BufferedBinaryMessage message)
            throws IOException {
        super.onFullBinaryMessage(channel, message);
    }

    @Override
    protected void onFullPingMessage(WebSocketChannel channel, BufferedBinaryMessage message)
            throws IOException {
        super.onFullPingMessage(channel, message);
    }

    @Override
    protected void onFullPongMessage(WebSocketChannel channel, BufferedBinaryMessage message)
            throws IOException {
        super.onFullPongMessage(channel, message);
    }

    @Override
    protected void onFullCloseMessage(WebSocketChannel channel, BufferedBinaryMessage message)
            throws IOException {
        super.onFullCloseMessage(channel, message);
    }

    @Override
    protected void onError(WebSocketChannel channel, Throwable error) {
        // 只是关闭连接
        super.onError(channel, error);
        if (userId != null) {
            // 从注册表中移除当前用户
            CHANNEL_MAP.remove(userId);
            // 将redis中当前用户的消息刷入MySQL中
            CacheUtils.redisToMySql(userId);
            LOGGER.info("已经断开连接[{}]", userId);
        }

        LOGGER.error("websocket出现错误,连接关闭：【{}】", error.getMessage());
    }
}
