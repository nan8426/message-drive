package com.message.drive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Describe 功能描述
 * @Author 袁江南
 * @Date 2019/12/12 15:25 wyx-app
 **/
@Configuration
public class DriveAsyncConfig {

    @Bean(name = "sendPool")
    public ThreadPoolExecutor withdrawAuditPool() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(128), new ThreadPoolExecutor.DiscardPolicy());
        return threadPoolExecutor;
    }

}
