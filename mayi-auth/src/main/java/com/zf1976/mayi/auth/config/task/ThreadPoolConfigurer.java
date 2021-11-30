/*
 * Copyright (c) 2021 zf1976
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

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
