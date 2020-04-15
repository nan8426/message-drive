package com.message.drive.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

/**
 * @Describe redis
 * @Author 袁江南
 * @Date 2019/11/18 15:59 message-drive
 **/
@Slf4j
@Configuration
@EnableCaching
public class JedisConfig {

    @Resource
    private JedisProperties prop;

    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(prop.getMaxTotal());
        config.setMaxIdle(prop.getMaxIdle());
        config.setMinIdle(prop.getMinIdle());
        config.setMaxWaitMillis(prop.getMaxWaitMillis());
        config.setTestOnBorrow(prop.isTestOnBorrow());
        config.setTestOnReturn(prop.isTestOnReturn());
        config.setTestWhileIdle(prop.isTestWhileIdle());
        config.setNumTestsPerEvictionRun(prop.getNumTestsPerEvictionRun());
        config.setMinEvictableIdleTimeMillis(prop.getMinEvictableIdleTimeMillis());
        config.setTimeBetweenEvictionRunsMillis(prop.getTimeBetweenEvictionRunsMillis());
        return config;
    }

    @Bean(name = "jedisConnectionFactory")
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setPort(prop.getPort());
        redisStandaloneConfiguration.setHostName(prop.getHost());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(prop.getPassword()));
        redisStandaloneConfiguration.setDatabase(prop.getDatabase());
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }

}
