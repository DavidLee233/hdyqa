package com.sysware.mainData.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
/**
 * @project npic
 * @description MainDataLocalHandlerConfig配置类，负责注册主数据模块配置相关运行组件与参数。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Configuration
@EnableScheduling
public class MainDataLocalHandlerConfig {

    /**
     * @description 执行mainDataTaskScheduler方法，完成主数据模块配置相关业务处理。
     * @params 无
     *
      * @return ThreadPoolTaskScheduler 类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Bean(name = "mainDataTaskScheduler")
    public ThreadPoolTaskScheduler mainDataTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("main-data-handler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        return scheduler;
    }
}