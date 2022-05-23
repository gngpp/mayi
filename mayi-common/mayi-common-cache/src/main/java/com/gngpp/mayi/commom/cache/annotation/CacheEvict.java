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

package com.gngpp.mayi.commom.cache.annotation;

import com.gngpp.mayi.commom.cache.enums.CacheImplement;

import java.lang.annotation.*;

/**
 * @author WINDOWS
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheEvict {

    /**
     * 缓存命名空间
     *
     * @return namespace
     */
    String namespace() default "";

    /**
     * 缓存依赖
     * @return /
     */
    String[] dependsOn() default {};

    /**
     * key
     *
     * @return key
     */
    String key() default "";

    /**
     * 支持清除后调用方法,只支持无参方法
     *
     * @return {@link String}
     */
    String[] postInvoke() default {};

    /**
     * 默认,当清除缓存时候默认清除所有（REDIS,CAFFEINE）缓存
     */
    boolean strategy() default true;

    /**
     * 缓存实现
     *
     * @return relation
     */
    CacheImplement implement() default CacheImplement.REDIS;
}
