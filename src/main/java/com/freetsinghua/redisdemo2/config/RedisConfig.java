package com.freetsinghua.redisdemo2.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.freetsinghua.redisdemo2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Properties;

import static org.apache.commons.pool2.impl.BaseObjectPoolConfig.*;

/**
 * @author z.tsinghua
 * @date 2018/10/29
 */
@Configuration
public class RedisConfig {

    private static final Properties PROPERTIES;
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);

    static {
        LOGGER.info("开始加载Redis配置文件...");
        ClassPathResource classPathResource =
                new ClassPathResource("config/redis-config.properties");
        PROPERTIES = new Properties();
        try {
            PROPERTIES.load(classPathResource.getInputStream());
            LOGGER.info("Redis配置文件加载完毕.");
        } catch (IOException e) {
            LOGGER.error("加载Redis配置文件失败：[{}]", e.getMessage());
        }
    }

    private Object stringToObject(String paramName, Object defaultValue, Class<?> clazz) {

        if (PROPERTIES == null || PROPERTIES.isEmpty()) {
            return defaultValue;
        }

        String property = PROPERTIES.getProperty(paramName);

        if (StringUtils.isEmpty(property)) {
            return defaultValue;
        } else {

            if (clazz.getName().equals(Boolean.class.getName())) {
                return Boolean.valueOf(property);
            }

            if (clazz.getName().equals(Integer.class.getName())) {
                return Integer.valueOf(property);
            }

            if (clazz.getName().equals(Long.class.getName())) {
                return Long.valueOf(property);
            }

            if (clazz.getName().equals(Float.class.getName())) {
                return Float.valueOf(property);
            }

            if (clazz.getName().equals(Double.class.getName())) {
                return Double.valueOf(property);
            }

            if (clazz.getName().equals(String.class.getName())) {
                return property;
            }
        }

        return defaultValue;
    }

    /**
     * 手动配置Redis连接工厂
     *
     * @return RedisConnectionFactory实例
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        int minIdle = 0;
        long timeBetweenEvictionRunsMillis = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
        boolean testOnBorrow = DEFAULT_TEST_ON_BORROW;
        boolean testWhileIdle = DEFAULT_TEST_WHILE_IDLE;
        int maxIdle = 5;
        long maxWaitMillis = DEFAULT_MAX_WAIT_MILLIS;
        int numTestsPerEvictionRun = DEFAULT_NUM_TESTS_PER_EVICTION_RUN;
        long minEvictableIdleTimeMillis = DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
        boolean blockWhenExhausted = DEFAULT_BLOCK_WHEN_EXHAUSTED;
        String evictionPolicyClassName = DEFAULT_EVICTION_POLICY_CLASS_NAME;
        long evictorShutdownTimeoutMillis = DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT_MILLIS;
        boolean fairness = DEFAULT_FAIRNESS;
        boolean lifo = DEFAULT_LIFO;
        boolean testOnCreate = DEFAULT_TEST_ON_CREATE;
        boolean testOnReturn = DEFAULT_TEST_ON_RETURN;

        LOGGER.info("开始获取Redis配置文件属性值...");
        minIdle = (int) stringToObject("minIdle", minIdle, Integer.class);
        timeBetweenEvictionRunsMillis =
                (long)
                        stringToObject(
                                "timeBetweenEvictionRunsMillis",
                                timeBetweenEvictionRunsMillis,
                                Long.class);
        testOnBorrow = (boolean) stringToObject("testOnBorrow", testOnBorrow, Boolean.class);
        testWhileIdle = (boolean) stringToObject("testWhileIdle", testWhileIdle, Boolean.class);
        maxIdle = (int) stringToObject("maxIdle", maxIdle, Integer.class);
        maxWaitMillis = (long) stringToObject("maxWaitMillis", maxWaitMillis, Long.class);
        numTestsPerEvictionRun =
                (int)
                        stringToObject(
                                "numTestsPerEvictionRun", numTestsPerEvictionRun, Integer.class);
        minEvictableIdleTimeMillis =
                (long)
                        stringToObject(
                                "minEvictableIdleTimeMillis",
                                minEvictableIdleTimeMillis,
                                Long.class);
        blockWhenExhausted =
                (boolean) stringToObject("blockWhenExhausted", blockWhenExhausted, Boolean.class);
        evictionPolicyClassName =
                (String)
                        stringToObject(
                                "evictionPolicyClassName", evictionPolicyClassName, String.class);
        evictorShutdownTimeoutMillis =
                (long)
                        stringToObject(
                                "evictorShutdownTimeoutMillis",
                                evictorShutdownTimeoutMillis,
                                Long.class);
        fairness = (boolean) stringToObject("fairness", fairness, Boolean.class);
        lifo = (boolean) stringToObject("lifo", lifo, Boolean.class);
        testOnCreate = (boolean) stringToObject("testOnCreate", testOnCreate, Boolean.class);
        testOnReturn = (boolean) stringToObject("testOnReturn", testOnReturn, Boolean.class);

        LOGGER.info("获取Redis配置文件属性值完毕.");

        LOGGER.info("开始配置Jedis池...");

        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestWhileIdle(testWhileIdle);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        jedisPoolConfig.setBlockWhenExhausted(blockWhenExhausted);
        jedisPoolConfig.setEvictionPolicyClassName(evictionPolicyClassName);
        jedisPoolConfig.setEvictorShutdownTimeoutMillis(evictorShutdownTimeoutMillis);
        jedisPoolConfig.setFairness(fairness);
        jedisPoolConfig.setLifo(lifo);
        jedisPoolConfig.setTestOnCreate(testOnCreate);
        jedisPoolConfig.setTestOnReturn(testOnReturn);

        LOGGER.info("配置Jedis池成功.");

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
        jedisConnectionFactory.setHostName("192.168.19.128");
        jedisConnectionFactory.setPort(6001);
        jedisConnectionFactory.setTimeout(30000);
        jedisConnectionFactory.setUsePool(true);
        jedisConnectionFactory.afterPropertiesSet();
        // 使用连接池

        JedisClientConfiguration clientConfiguration =
                jedisConnectionFactory.getClientConfiguration();
        boolean usePooling = clientConfiguration.isUsePooling();
        if (usePooling) {
            LOGGER.info("使用了连接池");
        }

        return jedisConnectionFactory;
    }

    @Bean
    public Jedis jedis() {

        return ((JedisConnectionFactory) redisConnectionFactory()).getShardInfo().createResource();
    }

    @Bean
    @Scope("prototype")
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) {

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 使用FastJson作为值的序列化工具
        RedisSerializer<Object> valueSerializer = new FastJsonRedisSerializer<>(Object.class);

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setBeanClassLoader(this.getClass().getClassLoader());

        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
