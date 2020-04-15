package com.message.drive.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

 /**
 * @Describe 分布式锁配置
 * @Author 袁江南
 * @Date 2019/11/18 15:59 message-drive
 **/
@Configuration
public class RedissonConfig {

    @Resource
    private JedisProperties prop;

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + prop.getHost() + ":" + prop.getPort())
                .setPassword(prop.getPassword()).setDatabase(prop.getDatabase());
        return Redisson.create(config);
    }

}
