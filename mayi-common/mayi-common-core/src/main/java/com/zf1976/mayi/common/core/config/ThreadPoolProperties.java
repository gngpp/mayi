/*
 *
 *  * Copyright (c) 2021 zf1976
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *  *
 *
 */

package com.zf1976.mayi.common.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 公用线程池配置
 *
 * @author mac
 * @date 2021/5/22
 */
@Component
@ConfigurationProperties(prefix = "executor")
public class ThreadPoolProperties {

    /**
     * 默认线程池大小
     */
    private Integer corePoolSize = Runtime.getRuntime()
                                          .availableProcessors();

    /**
     * 默认最大线程数
     */
    private Integer maxPoolSize = Runtime.getRuntime()
                                         .availableProcessors() << 2;

    /**
     * 默认队列大小
     */
    private Integer queueCapacity = 100;

    /**
     * 默认前缀
     */
    private String namePrefix = "Core-task-";

    /**
     * 线程存活时间
     */
    private Integer KeepAliveSeconds = 200;

    /**
     * 线程组名
     */
    private String threadGroupName = "Default-Group";

    /**
     * Bean名
     */
    private String beanName = "ThreadPoolTaskExecutor-Bean";

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getThreadGroupName() {
        return threadGroupName;
    }

    public void setThreadGroupName(String threadGroupName) {
        this.threadGroupName = threadGroupName;
    }

    public Integer getKeepAliveSeconds() {
        return KeepAliveSeconds;
    }

    public void setKeepAliveSeconds(Integer keepAliveSeconds) {
        KeepAliveSeconds = keepAliveSeconds;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }
}
