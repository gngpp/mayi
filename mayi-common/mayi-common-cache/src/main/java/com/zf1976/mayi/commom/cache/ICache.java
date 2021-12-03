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

package com.zf1976.mayi.commom.cache;


import java.util.function.Supplier;

/**
 * @author WINDOWS
 */
public interface ICache<K, V> {

    /**
     * 记录namespace缓存的健
     */
    String RECORD_NAMESPACE_KEY = "record-namespace-key";

    /**
     * 获取缓存值，缓存空间不存在则根据expired 创建缓存空间
     *
     * @param namespace 缓存空间
     * @param key       key
     * @param expired   过期时间
     * @param supplier  自定义加载缓存值
     * @return V
     */
    V getValueAndSupplier(String namespace, K key, Long expired, Supplier<V> supplier);

    /**
     * 获取缓存值，缓存空间不存在则根据默认配置 创建缓存空间
     *
     * @param namespace 缓存空间
     * @param key       key
     * @param supplier  自定义加载缓存值
     * @return V
     */
    V getValueAndSupplier(String namespace, K key, Supplier<V> supplier);

    /**
     * 获取缓存值，不做处理
     *
     * @param namespace 缓存空间
     * @param key       key
     * @return V
     */
    V getValue(String namespace, K key);

    /**
     * 根据缓存空间 设置缓存值
     *
     * @param namespace 缓存空间
     * @param key       key
     * @param expired   过期时间
     * @param value     V
     */
    void setValue(String namespace, K key, V value, Long expired);

    /**
     * 根据缓存空间 设置缓存值
     *
     * @param namespace 缓存空间
     * @param key       key
     * @param value     V
     */
    void setValue(String namespace, K key, V value);

    /**
     * 根据命名空间删除缓存
     *
     * @param namespace 缓存空间
     */
    void invalidate(String namespace);

    /**
     * 使某缓存空间某缓存失效
     *
     * @param namespace 缓存空间
     * @param key       key
     */
    void invalidate(String namespace, K key);

    /**
     * 清除所有缓存
     */
    void invalidateAll();

    /**
     * 强制格式化命名空间
     *
     * @param namespace 命名空间
     * @return {@link String}
     */
    String formatNamespace(String namespace);

}
