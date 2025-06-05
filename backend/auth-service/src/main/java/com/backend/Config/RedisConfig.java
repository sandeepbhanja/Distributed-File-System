package com.backend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisConfig {

    @Value("${redis.connectionIp}")
    String connectionIp;

    @Bean
    public JedisPool jedisPool(){
        return new JedisPool(connectionIp,6379);
    }

}
