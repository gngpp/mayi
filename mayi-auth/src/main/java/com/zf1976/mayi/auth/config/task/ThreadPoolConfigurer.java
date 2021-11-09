package com.zf1976.mayi.auth.config.task;

import com.zf1976.mayi.common.core.config.ThreadPoolProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author mac
 * @date 2021/5/22
 */
@Configuration
public class ThreadPoolConfigurer {

    private final ThreadPoolProperties poolProperties;

    public ThreadPoolConfigurer(ThreadPoolProperties poolProperties) {
        this.poolProperties = poolProperties;
    }

    /**
     * * 配置线程池
     *
     * @return {@link ThreadPoolTaskExecutor}
     */
    @Bean
    public ThreadPoolTaskExecutor TaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //此方法返回可用处理器的虚拟机的最大数量; 不小于1
        taskExecutor.setBeanName(poolProperties.getBeanName());
        taskExecutor.setThreadGroupName(poolProperties.getThreadGroupName());
        taskExecutor.setCorePoolSize(poolProperties.getCorePoolSize());
        taskExecutor.setMaxPoolSize(poolProperties.getMaxPoolSize());
        taskExecutor.setQueueCapacity(poolProperties.getQueueCapacity());
        taskExecutor.setKeepAliveSeconds(poolProperties.getKeepAliveSeconds());
        taskExecutor.setThreadNamePrefix(poolProperties.getNamePrefix());//线程名称前缀
        // 线程池对拒绝任务（无线程可用）的处理策略，目前只支持AbortPolicy、CallerRunsPolicy；默认为后者
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }

}
