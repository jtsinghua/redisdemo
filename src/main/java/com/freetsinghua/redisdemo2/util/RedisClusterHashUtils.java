package com.freetsinghua.redisdemo2.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.freetsinghua.redisdemo2.config.RedisClusterConfig;
import com.freetsinghua.redisdemo2.constant.Constants;
import com.freetsinghua.redisdemo2.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import redis.clients.jedis.JedisCluster;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 集群版redis操作API
 *
 * @author z.tsinghua
 * @date 2018/11/1
 */
@Slf4j
public class RedisClusterHashUtils implements RedisHashUtils {

    private static final JedisCluster JEDIS = initJedisCluster();
    private static final String HASH_KEY = Constants.MESSAGE_HASH_KEY.getValue();

    /**
     * 初始化JedisCluster实例
     *
     * @return 返回JedisCluster实例
     */
    private static JedisCluster initJedisCluster() {

        JedisCluster jedisCluster = BeanManager.getJedisCluster();
        if (jedisCluster == null) {
            jedisCluster = new RedisClusterConfig().jedisCluster();
        }

        return jedisCluster;
    }

    /**
     * 批量操作
     *
     * @param map 需要存入Redis的数据
     */
    @Override
    public void putAll(Map<String, Message> map) {
        Map<String, String> stringMap = convertToStringMap(map);

        log.info("开始批量插入!");
        JEDIS.hmset(HASH_KEY, stringMap);
        log.info("批量插入成功!");
    }

    /**
     * Map<string, message>转化为string map
     *
     * @param map 转化目标
     * @return 转化结果
     */
    private Map<String, String> convertToStringMap(Map<String, Message> map) {
        Map<String, String> stringMap = new HashMap<>(0);

        for (Map.Entry<String, Message> entry : map.entrySet()) {
            stringMap.put(entry.getKey(), JSON.toJSONString(entry.getValue()));
        }

        return stringMap;
    }

    /**
     * 频繁开启关闭连接，效率低下，若是批量操作，选用putAll
     *
     * @param key key 键
     * @param value value 值
     */
    @Override
    public synchronized void put(String key, Object value) {

        JEDIS.hset(HASH_KEY, key, JSON.toJSONString(value));
        log.info("{}保存成功!", key);
    }

    /**
     * 根据键获取值
     *
     * @param key 键
     * @return 返回结果，若是对应的值不存在，返回一个新的Message对象
     * @apiNote 不会返回null， 但是可能为空，也就是Message实例的id等为null
     */
    @Override
    @NotNull
    public Message get(String key) {
        try {
            String mapString = JEDIS.hget(HASH_KEY, key);
            Message message = JSON.parseObject(mapString, Message.class);
            log.info("键【{}】对应的值获取成功!", key);
            return message;
        } catch (Exception e) {
            log.error("键【{}】对应的值获取失败：{}", key, e.getMessage(), e);
            return new Message();
        }
    }

    /**
     * 返回messages哈希表中所有的entry
     *
     * @return 返回结果
     */
    @Override
    @NotNull
    public Map<String, Message> entries() {
        Map<String, String> map = JEDIS.hgetAll(HASH_KEY);
        Map<String, Message> messageMap = new HashMap<>(0);

        for (Map.Entry<String, String> entry : map.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();
            Message message = jsonToMessage(value);
            messageMap.put(key, message);
        }

        return messageMap;
    }

    private Message jsonToMessage(String jsonStr) {

        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String id = jsonObject.getString("id");
        String src = jsonObject.getString("src");
        String dest = jsonObject.getString("dest");
        String msg = jsonObject.getString("msg");
        String remark = jsonObject.getString("remark");

        return new Message(id, src, dest, msg, remark);
    }

    /**
     * 返回message哈希表中键包含id字符串的entry
     *
     * @param pattern 需要匹配的字符串
     * @return 返回结果
     */
    @Override
    @NotNull
    public Map<String, Message> entries(String pattern) {

        @NotNull Map<String, Message> entries = entries();

        if (StringUtils.isEmpty(pattern)) {
            return entries;
        }

        Map<String, Message> map = new HashMap<>(entries.size());

        for (Map.Entry<String, Message> entry : entries.entrySet()) {
            String key = entry.getKey();
            if (key.contains(pattern)) {
                map.put(key, entry.getValue());
            }
        }

        return map;
    }

    @Override
    public long size() {

        return JEDIS.hgetAll(HASH_KEY).size();
    }

    /**
     * 删除方法
     *
     * @param hashKeys 要删除的key
     * @return 返回删除了多少条
     */
    @Override
    @Nullable
    public Long delete(String... hashKeys) {

        if (hashKeys == null || hashKeys.length == 0) {
            return 0L;
        }

        try {
            return JEDIS.hdel(HASH_KEY, hashKeys);
        } catch (Exception e) {
            log.error("删除失败：{}", e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    @NotNull
    public Set<String> keySet() {
        return entries().keySet();
    }

    @Override
    public Set<String> keySet(String pattern) {
        return entries(pattern).keySet();
    }
}
