package com.freetsinghua.redisdemo2.util;

import com.freetsinghua.redisdemo2.constant.Constants;
import com.freetsinghua.redisdemo2.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 单机版Redis操作
 *
 * @author z.tsinghua
 * @date 2018/10/30
 */
public class RedisStandAloneHashUtils implements RedisHashUtils {

    private static final RedisTemplate<String, Object> REDIS_TEMPLATE =
            BeanManager.getRedisTemplate();

    private static final BoundHashOperations<String, Object, Object> HASH_OPERATIONS =
            REDIS_TEMPLATE.boundHashOps(Constants.MESSAGE_HASH_KEY.getValue());

    private static Logger logger = LoggerFactory.getLogger(RedisHashUtils.class);

    /**
     * map解析为Message对象
     *
     * @param map 要解析的map对象
     * @return 返回Message实例
     */
    @NotNull
    private Message mapToMessage(Map<Object, Object> map) {

        String id = (String) map.get("id");
        String src = (String) map.get("src");
        String dest = (String) map.get("dest");
        String msg = (String) map.get("msg");
        String remark = (String) map.get("remark");

        Message message = new Message(src, dest, msg);
        message.setId(id);
        message.setRemark(remark);

        return message;
    }

    @Override
    public void putAll(Map<String, Message> map) {
        HASH_OPERATIONS.putAll(map);
    }

    @Override
    public void put(String key, Object value) {
        HASH_OPERATIONS.put(key, value);
    }

    @Override
    public @NotNull Message get(String key) {
        Object o = HASH_OPERATIONS.get(key);

        if (o instanceof Message) {

            return (Message) o;
        }
        if (o instanceof Map) {
            Map map = (Map) o;
            @SuppressWarnings("unchecked")
            @NotNull
            Message message = mapToMessage(map);
            return message;
        }

        return null;
    }

    @Override
    public @NotNull Map<String, Message> entries() {

        Map<Object, Object> entries =
                REDIS_TEMPLATE.opsForHash().entries(Constants.MESSAGE_HASH_KEY.getValue());
        Map<String, Message> messageMap = new HashMap<>(0);

        for (Map.Entry<Object, Object> entry : entries.entrySet()) {

            if (entry.getKey() instanceof String) {
                String key = (String) entry.getKey();

                if (entry.getValue() instanceof Map) {
                    Map map = (Map) entry.getValue();
                    @SuppressWarnings("unchecked")
                    @NotNull
                    Message message = mapToMessage(map);
                    messageMap.put(key, message);
                }
            }
        }

        return messageMap;
    }

    @Override
    public @NotNull Map<String, Message> entries(String pattern) {
        @NotNull Map<String, Message> entries = entries();
        Map<String, Message> map = new HashMap<>(0);

        String key;
        for (Map.Entry<String, Message> entry : entries.entrySet()) {
            key = entry.getKey();
            if (key.contains(pattern)) {
                map.put(key, entry.getValue());
            }
        }

        return map;
    }

    @Override
    public Long delete(String... hashKeys) {

        if (hashKeys == null || hashKeys.length == 0) {
            return 0L;
        }

        return HASH_OPERATIONS.delete(hashKeys);
    }

    @Override
    public @NotNull Set<String> keySet() {
        return entries().keySet();
    }

    @Override
    public Set<String> keySet(String pattern) {
        return entries(pattern).keySet();
    }

    @Override
    public long size() {
        return keySet().size();
    }
}
