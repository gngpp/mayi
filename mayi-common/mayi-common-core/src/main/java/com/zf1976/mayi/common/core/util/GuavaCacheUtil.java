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

package com.zf1976.mayi.common.core.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 公共缓存
 *
 * @author WINDOWS
 */
@SuppressWarnings("unchecked")
public class GuavaCacheUtil {

    /**
     * 本地缓存最大数量
     */
    private static final long LOCAL_CAFFEINE_MAXIMUM_SIZE = 100_0000;

    /**
     * 固定时间缓存 - 1分钟
     */
    private static final Cache<Object, Object> FIXED_ONE_MINUTES_CACHE = CacheBuilder.newBuilder()
                                                                                     .maximumSize(LOCAL_CAFFEINE_MAXIMUM_SIZE)
                                                                                     .expireAfterWrite(1,TimeUnit.MINUTES)
                                                                                     .build();
    /**
     * 固定时间缓存 - 1小时
     */
    private static final Cache<Object, Object> FIXED_ONE_HOUR_CACHE = CacheBuilder.newBuilder()
                                                                                  .maximumSize(LOCAL_CAFFEINE_MAXIMUM_SIZE)
                                                                                  .expireAfterWrite(1, TimeUnit.HOURS)
                                                                                  .build();
    /**
     * 固定时间缓存 - 1天
     */
    private static final Cache<Object, Object> FIXED_ONE_DAY_CACHE = CacheBuilder.newBuilder()
                                                                                 .maximumSize(LOCAL_CAFFEINE_MAXIMUM_SIZE)
                                                                                 .expireAfterWrite(1, TimeUnit.DAYS)
                                                                                 .build();

    /**
     * 获取缓存、无则设值 - 1分钟
     *
     * @param key      key
     * @param supplier 自定义加载缓存值
     * @param <T>      返回类型
     * @return R
     */
    public static <T> T getFixedOneMinutes(String key, Supplier<T> supplier) {
        return getCustomVariable(FIXED_ONE_MINUTES_CACHE, key, supplier);
    }

    /**
     * 获取缓存 - 1分钟
     *
     * @param key key
     * @param <T> 返回类型
     * @return T
     */
    public static <T> T getFixedOneMinutes(String key) {
        return getDefaultVariable(key, FIXED_ONE_MINUTES_CACHE);
    }

    /**
     * 缓存设值 - 1分钟
     *
     * @param key   key
     * @param value value
     */
    public static void setFixedOneMinutes(String key, Object value) {
        FIXED_ONE_MINUTES_CACHE.put(key, value);
    }

    /**
     * 删除缓存 - 1分钟
     *
     * @param key key
     */
    public static void delFixedOneMinutes(String key) {
        FIXED_ONE_MINUTES_CACHE.invalidate(key);
    }


    /**
     * 获取缓存、无则设值 - 1小时
     *
     * @param key      key
     * @param supplier 自定义返回值
     * @param <T>      返回类型
     * @return T
     */
    public static <T> T getFixedOneHours(String key, Supplier<T> supplier) {
        return getCustomVariable(FIXED_ONE_HOUR_CACHE, key, supplier);
    }

    /**
     * 获取缓存 - 1小时
     *
     * @param key key
     * @param <T> 返回类型
     * @return T
     */
    public static <T> T getFixedOneHours(String key) {
        return getDefaultVariable(key, FIXED_ONE_HOUR_CACHE);
    }

    /**
     * 缓存设值 - 1小时
     *
     * @param key   key
     * @param value value
     */
    public static void setFixedOneHours(String key, Object value) {
        FIXED_ONE_HOUR_CACHE.put(key, value);
    }

    /**
     * 删除缓存 - 1小时
     *
     * @param key key
     */
    public static void delFixedOneHours(String key) {
        FIXED_ONE_HOUR_CACHE.invalidate(key);
    }

    /**
     * 获取缓存、无则设值 - 1天
     *
     * @param key      key
     * @param supplier 自定义加载返回值
     * @param <T>      返回类型
     * @return T
     */
    public static <T> T getFixedOneDays(String key, Supplier<T> supplier) {
        return getCustomVariable(FIXED_ONE_DAY_CACHE, key, supplier);
    }

    /**
     * 获取缓存 - 1天
     *
     * @param key key
     * @param <T> 返回类型
     * @return T
     */
    public static <T> T getFixedOneDays(String key) {
        return getDefaultVariable(key, FIXED_ONE_DAY_CACHE);
    }

    /**
     * 缓存设值 - 1天
     *
     * @param key   key
     * @param value value
     */
    public static void setFixedOneDays(String key, Object value) {
        FIXED_ONE_DAY_CACHE.put(key, value);
    }

    /**
     * 删除缓存 - 1天
     *
     * @param key key
     */
    public static void delFixedOneDays(String key) {
        FIXED_ONE_DAY_CACHE.invalidate(key);
    }

    /**
     * 获取不满足条件 自定义返回值
     *
     * @param caffeineCache cache obj
     * @param key           key
     * @param supplier      加载自定义返回值
     * @param <T>           返回类型
     * @return T
     */
    private static <T> T getCustomVariable(Cache<Object, Object> caffeineCache, String key, Supplier<T> supplier) {
        // 获取缓存值
        Object value = getDefaultVariable(key, caffeineCache);
        if (value != null) {
            return (T) value;
        }
        // 自定义返回缓存值
        T result = supplier.get();
        if (result != null) {
            // 设置本地缓存
            caffeineCache.put(key, result);
        }
        return result;
    }

    /**
     * 获取默认缓存值
     *
     * @param key           key
     * @param caffeineCache cache obj
     * @param <T>           返回类型
     * @return T
     */
    private static <T> T getDefaultVariable(String key, Cache<Object, Object> caffeineCache) {
        assert caffeineCache != null;
        if (ObjectUtils.isEmpty(key)) {
            return null;
        }
        return (T) caffeineCache.getIfPresent(key);
    }
}
