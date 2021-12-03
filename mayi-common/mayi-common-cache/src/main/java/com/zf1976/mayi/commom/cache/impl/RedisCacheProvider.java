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

package com.zf1976.mayi.commom.cache.impl;

import com.zf1976.mayi.commom.cache.ICache;
import com.zf1976.mayi.commom.cache.property.CacheProperties;
import com.zf1976.mayi.common.core.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 如果命名空间缓存保持活跃状态，那么剩余时间将被更新
 *
 * @author mac
 * 2021/3/14
 **/
public class RedisCacheProvider<K, V> implements ICache<K, V> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final CacheProperties properties;
    private final RedisTemplate<Object, Object> redisTemplate;

    public RedisCacheProvider(CacheProperties properties, RedisTemplate<Object, Object> redisTemplate) {
        this.properties = properties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void setValue(String namespace, K key, V value, Long expired) {
        final String formatNamespace = this.formatNamespace(namespace);
        this.redisTemplate.opsForHash().put(formatNamespace, key, value);
        this.setExpired(formatNamespace, expired);
    }

    @Override
    public void setValue(String namespace, K key, V value) {
        this.setValue(namespace, key, value, properties.getExpireAlterWrite());
    }

    @Override
    public V getValueAndSupplier(String namespace, K key, Long expired, Supplier<V> supplier) {
        V value = this.getValueAndUpdate(namespace, key, expired);
        if (value != null) {
            return value;
        }
        value = supplier.get();
        if (value != null) {
            this.setValue(namespace, key, value, expired);
        }
        return value;
    }

    @Override
    public V getValueAndSupplier(String namespace, K key, Supplier<V> supplier) {
        V value = this.getValue(namespace, key);
        if (value != null) {
            return value;
        }
        value = supplier.get();
        if (value != null) {
            this.setValue(namespace, key, value);
        }
        return value;
    }

    private void setExpired(String namespace, Long expired) {
        this.redisTemplate.expire(namespace,
                expired == null || expired < properties.getExpireAlterWrite()? properties.getExpireAlterWrite() : expired,
                TimeUnit.MINUTES);
    }

    public V getValueAndUpdate(String namespace, K key, Long expired) {
        @SuppressWarnings("unchecked")
        V value = (V) this.redisTemplate.opsForHash().get(this.formatNamespace(namespace), key);
        this.setExpired(namespace, expired);
        return value;
    }

    @Override
    public V getValue(String namespace, K key) {
        @SuppressWarnings("unchecked")
        V value = (V) this.redisTemplate.opsForHash().get(this.formatNamespace(namespace), key);
        return value;
    }

    @Override
    public void invalidate(String namespace) {
        log.debug("the cache namespace:" + namespace + " has been destroyed");
        this.redisTemplate.delete(this.formatNamespace(namespace));
    }

    @Override
    public void invalidate(String namespace, K key) {
        log.debug("the key：{} of the cached namespace：{} has been destroyed", namespace, key);
        this.redisTemplate.opsForHash().delete(namespace, key);
    }

    @Override
    public void invalidateAll() {
        final Set<String> keys = RedisUtil.scanKeys(this.formatNamespace("*"));
        if (!CollectionUtils.isEmpty(keys)) {
            this.redisTemplate.delete(keys);
        }
    }

    @Override
    public String formatNamespace(String namespace) {
        return this.properties.getKeyPrefix().concat("[" + namespace + "]");
    }
}
