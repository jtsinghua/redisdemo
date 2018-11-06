package com.freetsinghua.redisdemo2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * @author z.tsinghua
 * @date 2018/11/1
 */
@Configuration
public class RedisClusterConfig {

    @Bean
    public RedisClusterConnection redisClusterConnection(JedisCluster jedisCluster) {

        return new JedisClusterConnection(jedisCluster);
    }

    @Bean
    public JedisCluster jedisCluster() {

        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("localhost", 6000));
        nodes.add(new HostAndPort("localhost", 6001));
        nodes.add(new HostAndPort("localhost", 6002));
        nodes.add(new HostAndPort("localhost", 6003));
        nodes.add(new HostAndPort("localhost", 6004));
        nodes.add(new HostAndPort("localhost", 6005));

        return new JedisCluster(nodes);
    }
}
