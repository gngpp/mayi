/*
 *
 *  * Copyright (c) 2021 gngpp
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

package com.gngpp.mayi.common.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 */
@Component
public class RedisUtil {

    private final static Logger LOG = LoggerFactory.getLogger(RedisUtil.class);

    private static StringRedisTemplate redisTemplate;

    /**
     * get操作
     *
     * @param key 键
     * @return {@link String}
     */
    public static String get(String key) {
        return redisTemplate.opsForValue()
                            .get(key);
    }

    /**
     * set操作
     *
     * @param key      键
     * @param value    值
     * @param expired  过期时间
     * @param timeUnit 时间单位
     */
    public static void set(String key, String value, long expired, TimeUnit timeUnit) {
        redisTemplate.opsForValue()
                     .set(key, value, expired, timeUnit);
    }

    /**
     * delete操作
     *
     * @param key 键
     * @return {@link Boolean}
     */
    public static Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * set操作
     *
     * @param prefix   前缀
     * @param key      键
     * @param value    值
     * @param expired  过期时间
     * @param timeUnit 时间单位
     */
    public static void set(String prefix, String key, String value, long expired, TimeUnit timeUnit) {
        redisTemplate.opsForValue()
                     .set(prefix + key, value, expired, timeUnit);
    }

    /**
     * get操作
     *
     * @param prefix 前缀
     * @param key    键
     * @return {@link String}
     */
    public static String get(String prefix, String key) {
        return redisTemplate.opsForValue()
                            .get(prefix + key);
    }

    /**
     * delete操作
     *
     * @param prefix 前缀
     * @param key    键
     * @return {@link Boolean}
     */
    public static Boolean delete(String prefix, String key) {
        return redisTemplate.delete(prefix + key);
    }

    /**
     * delete操作
     *
     * @param keys 键集合
     * @return {@link Long}
     */
    public static Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 获取所有模式匹配key
     *
     * @param pattern 模式
     * @return keys
     */
    public static Set<String> scanKeys(String pattern) {
        final ScanOptions scanOptions = ScanOptions.scanOptions()
                                                   .match(pattern)
                                                   .build();
        Set<String> keys = new HashSet<>();
        final RedisConnectionFactory connectionFactory = redisTemplate.getRequiredConnectionFactory();
        RedisConnection connection = null;
        try {
            connection = connectionFactory.getConnection();
            final Cursor<byte[]> cursor = connection.scan(scanOptions);
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
            return keys;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.getCause());
        } finally {
            RedisConnectionUtils.releaseConnection(connection, connectionFactory, true);
        }
        return keys;
    }

    /**
     * 根据模式匹配 分页查询key
     *
     * @param pattern 模式
     * @param page 页
     * @param pageSize 页大小
     * @return /
     */
    public static Set<String> scanKeysForPage(String pattern, int page, int pageSize) {
        final ScanOptions scanOptions = ScanOptions.scanOptions()
                                                   .match(pattern)
                                                   .build();
        Set<String> keys = new HashSet<>();
        final RedisConnectionFactory connectionFactory = redisTemplate.getRequiredConnectionFactory();
        RedisConnection connection = null;
        try {
            connection = connectionFactory.getConnection();
            final Cursor<byte[]> cursor = connection.scan(scanOptions);
            int tempIndex = 0;
            int fromIndex = page * pageSize;
            int toIndex = fromIndex + pageSize;
            while (cursor.hasNext()) {
                if (tempIndex >= toIndex) {
                    break;
                }
                if (tempIndex >= fromIndex) {
                    keys.add(new String(cursor.next()));
                    ++tempIndex;
                    continue;
                }
                ++tempIndex;
            }
            return keys;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e.getCause());
        } finally {
            RedisConnectionUtils.releaseConnection(connection, connectionFactory, true);
        }
        return keys;
    }

    @Autowired
    public void setRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        RedisUtil.redisTemplate = stringRedisTemplate;
    }

}
