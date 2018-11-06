package com.freetsinghua.redisdemo2.util;

import com.freetsinghua.redisdemo2.model.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单缓存
 *
 * @author z.tsinghua
 * @date 2018/10/31
 */
public final class Cache {

    private static final Map<String, Message> CACHE = new ConcurrentHashMap<>(5001, 0.75f);

    private Cache() {}

    public static Map<String, Message> getCache() {
        return Cache.CACHE;
    }
}
