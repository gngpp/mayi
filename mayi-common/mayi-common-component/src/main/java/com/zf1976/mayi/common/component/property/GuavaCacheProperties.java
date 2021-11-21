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
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.common.component.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author WINDOWS
 */
@Component
@ConfigurationProperties(prefix = "cache")
public class GuavaCacheProperties {
    /**
     * 并发级别
     */
    private Integer concurrencyLevel;
    /**
     * 初始化容量
     */
    private Integer initialCapacity;
    /**
     * 最大容量
     */
    private Integer maximumSize;
    /**
     * 写入后过期时间 单位/seconds
     */
    private Long expireAlterWrite;
    /**
     * key前缀
     */
    private String keyPrefix;

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public GuavaCacheProperties setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
        return this;
    }

    public Integer getConcurrencyLevel() {
        return concurrencyLevel;
    }

    public GuavaCacheProperties setConcurrencyLevel(Integer concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    public Integer getInitialCapacity() {
        return initialCapacity;
    }

    public GuavaCacheProperties setInitialCapacity(Integer initialCapacity) {
        this.initialCapacity = initialCapacity;
        return this;
    }

    public Integer getMaximumSize() {
        return maximumSize;
    }

    public GuavaCacheProperties setMaximumSize(Integer maximumSize) {
        this.maximumSize = maximumSize;
        return this;
    }

    public Long getExpireAlterWrite() {
        return expireAlterWrite;
    }

    public GuavaCacheProperties setExpireAlterWrite(Long expireAlterWrite) {
        this.expireAlterWrite = expireAlterWrite;
        return this;
    }

    @Override
    public String toString() {
        return "CacheProperties{" +
                "concurrencyLevel=" + concurrencyLevel +
                ", initialCapacity=" + initialCapacity +
                ", maximumSize=" + maximumSize +
                ", expireAlterWrite=" + expireAlterWrite +
                ", keyPrefix='" + keyPrefix + '\'' +
                '}';
    }
}
