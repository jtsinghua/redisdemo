package com.freetsinghua.redisdemo2.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * @author z.tsinghua
 * @date 2018/10/29
 */
@Configuration
public class BeanManager {
    private static JdbcTemplate jdbcTemplate;
    private static RedisTemplate<String, Object> redisTemplate;
    private static Jedis jedis;
    private static JedisCluster jedisCluster;
    private static FileLogger fileLogger;

    public static JdbcTemplate getJdbcTemplate() {
        return BeanManager.jdbcTemplate;
    }

    public static RedisTemplate<String, Object> getRedisTemplate() {
        return BeanManager.redisTemplate;
    }

    public static Jedis getJedis() {
        return BeanManager.jedis;
    }

    public static FileLogger getFileLogger() {
        return BeanManager.fileLogger;
    }

    public static JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    @Autowired
    public void manager(
            JdbcTemplate jdbcTemplate,
            RedisTemplate<String, Object> redisTemplate,
            Jedis jedis,
            FileLogger fileLogger,
            JedisCluster jedisCluster) {
        BeanManager.jdbcTemplate = jdbcTemplate;
        BeanManager.redisTemplate = redisTemplate;
        BeanManager.jedis = jedis;
        BeanManager.fileLogger = fileLogger;
        BeanManager.jedisCluster = jedisCluster;
    }
}
