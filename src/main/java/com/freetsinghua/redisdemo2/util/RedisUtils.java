package com.freetsinghua.redisdemo2.util;

/**
 * 在此类中，配置是使用单机版还是集群版
 *
 * @author z.tsinghua
 * @date 2018/11/1
 */
public class RedisUtils {
    /** 在这几配置是需要使用单机版还是集群版 */
    private static final RedisHashUtils REDIS_HASH_UTILS = new RedisClusterHashUtils();

    public static RedisHashUtils getRedisHashUtils() {
        return RedisUtils.REDIS_HASH_UTILS;
    }
}
