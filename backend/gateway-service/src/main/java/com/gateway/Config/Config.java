package com.gateway.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.JedisPool;

@Configuration
public class Config {

    @Value("${redis.connectionIp}")
    String connectionIp;

    @Bean
    public JedisPool jedisPool(){
        return new JedisPool(connectionIp,6379);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
