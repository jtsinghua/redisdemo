package com.freetsinghua.redisdemo2.util;

import com.freetsinghua.redisdemo2.model.Message;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

/**
 * Redis哈希操作工具
 *
 * @author z.tsinghua
 * @date 2018/11/1
 */
public interface RedisHashUtils {
    /**
     * 批量存入Redis中
     *
     * @param map 数据
     */
    void putAll(Map<String, Message> map);

    /**
     * 存入redis中
     *
     * @param key 要存入的键
     * @param value 要存入的值
     */
    void put(String key, Object value);

    /**
     * 根据key获取相应的value
     *
     * @param key 指定的key
     * @return 返回value
     */
    @NotNull
    Message get(String key);

    /**
     * 返回指定哈希表中的所有对象
     *
     * @return 返回结果
     */
    @NotNull
    Map<String, Message> entries();

    /**
     * 返回匹配指定pattern的key的对象
     *
     * @param pattern 指定的pattern
     * @return 返回结果
     */
    @NotNull
    Map<String, Message> entries(String pattern);

    /**
     * 删除
     *
     * @param hashKeys 要删除的key
     * @return 返回删除结果
     */
    @Nullable
    Long delete(String... hashKeys);

    /**
     * 返回哈希表的key集合
     *
     * @return 返回结果
     */
    @NotNull
    Set<String> keySet();

    /**
     * 返回匹配指定pattern的keys
     *
     * @param pattern 模式
     * @return 返回满足条件的key集合
     */
    Set<String> keySet(String pattern);

    /**
     * 返回Redis中，哈希表messages中键值对数量
     *
     * @return 数量
     */
    long size();
}
