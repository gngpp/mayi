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

package com.zf1976.mayi.commom.cache;


import com.zf1976.mayi.commom.cache.annotation.CacheConfig;
import com.zf1976.mayi.commom.cache.annotation.CacheEvict;
import com.zf1976.mayi.commom.cache.annotation.CachePut;
import com.zf1976.mayi.commom.cache.enums.CacheImplement;
import com.zf1976.mayi.commom.cache.handler.SpringElExpressionHandler;
import com.zf1976.mayi.commom.cache.impl.GuavaCacheProvider;
import com.zf1976.mayi.commom.cache.impl.RedisCacheProvider;
import com.zf1976.mayi.commom.cache.property.CacheProperties;
import com.zf1976.mayi.common.core.util.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.*;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author WINDOWS
 */
@Aspect
@Component
@Order(0)
public class CacheAsideAspect extends AbstractCacheAsideLock {

    private static final Logger log = LoggerFactory.getLogger("[CacheAsideAspect]");
    private final SpringElExpressionHandler handler = new SpringElExpressionHandler();
    private Map<CacheImplement, ICache<Object, Object>> cacheProviderMap;

    public CacheAsideAspect(RedisTemplate<Object, Object> jdkRedisTemplate, CacheProperties guavaCacheProperties) {
        this.addProvider(CacheImplement.CAFFEINE, new GuavaCacheProvider<>(guavaCacheProperties));
        this.addProvider(CacheImplement.REDIS, new RedisCacheProvider<>(guavaCacheProperties, jdkRedisTemplate));
        this.checkStatus();
    }

    public static <T> T[] clone(T[] array) {
        return array == null ? null : array.clone();
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] addAll(T[] array1, T... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        } else {
            Class<?> type1 = array1.getClass()
                                   .getComponentType();
            T[] joinedArray = (T[]) Array.newInstance(type1, array1.length + array2.length);
            System.arraycopy(array1, 0, joinedArray, 0, array1.length);

            try {
                System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
                return joinedArray;
            } catch (ArrayStoreException var6) {
                log.error(var6.getMessage(), var6);
                Class<?> type2 = array2.getClass()
                                       .getComponentType();
                if (!type1.isAssignableFrom(type2)) {
                    throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName(), var6);
                } else {
                    throw var6;
                }
            }
        }
    }

    /**
     * 更新调用
     *
     * @param joinPoint  切点
     * @param annotation 注解
     * @return {@link Object}
     */
    @Around("@annotation(com.zf1976.mayi.commom.cache.annotation.CacheEvict) && @annotation(annotation)")
    public Object remove(ProceedingJoinPoint joinPoint, CacheEvict annotation) throws Throwable {
        // Get the proxy class
        Class<?> targetClass = this.extractTargetClass(joinPoint.getThis());
        // Cache configuration
        CacheConfig cacheConfig = targetClass.getAnnotation(CacheConfig.class);
        List<String> methodList = Arrays.stream(addAll(annotation.postInvoke(), cacheConfig != null ? cacheConfig.postInvoke() : new String[0]))
                                        .distinct()
                                        .collect(Collectors.toUnmodifiableList());
        return this.doLockAndUpdate(
                joinPoint,
                unused -> this.clearCache(joinPoint, annotation, cacheConfig),
                unused -> this.executeMethodList(joinPoint, targetClass, methodList));
    }

    /**
     * 查询调用
     *
     * @param joinPoint  切点
     * @param annotation 注解
     * @return {@link Object}
     */
    @Around("@annotation(com.zf1976.mayi.commom.cache.annotation.CachePut) && @annotation(annotation))")
    public Object put(ProceedingJoinPoint joinPoint, CachePut annotation) throws Throwable {
        // Get the proxy class
        Class<?> targetClass = this.extractTargetClass(joinPoint.getThis());
        // Proxy method
        Method method = this.handler.filterMethod(joinPoint);
        // Cache configuration
        CacheConfig classAnnotation = targetClass.getAnnotation(CacheConfig.class);
        // namespaces
        String namespace = classAnnotation == null ? annotation.namespace() : classAnnotation.namespace();
        // Cache Key
        String cacheKey = this.extractCacheKey(method, joinPoint.getArgs(), annotation.key());
        if (StringUtils.hasLength(annotation.dynamicsKey())) {
            String dynamicsKey = this.extractCacheKey(method, joinPoint.getArgs(), annotation.dynamicsKey());
            cacheKey = cacheKey.concat(dynamicsKey);
        }
        boolean lockCondition = false;
        // DB data is being updated
        if (lock.isLocked()) {
            lockCondition = lock.tryLock(DEFAULT_LOCK_TIME, TimeUnit.SECONDS);
            // timeout
            if (!lockCondition) {
                return null;
            }
        }
        try {
            return this.cacheProviderMap.get(annotation.implement())
                                        .getValueAndSupplier(namespace, cacheKey, annotation.expired(), () -> {
                                            try {
                                                return joinPoint.proceed();
                                            } catch (Throwable e) {
                                                throw new RuntimeException(e);
                                            }
                                        });
        } finally {
            if (lockCondition) {
                lock.unlock();
            }
        }
    }

    /**
     * 获取被代理真实Class
     *
     * @param proxyObj 代理对象
     * @return {@link Class<>}
     */
    private Class<?> extractTargetClass(Object proxyObj) {
        return AopProxyUtils.ultimateTargetClass(proxyObj);
    }

    private void clearCache(ProceedingJoinPoint joinPoint, CacheEvict annotation, CacheConfig cacheConfig) {
        // Proxy method
        Method method = this.handler.filterMethod(joinPoint);
        // namespaces
        String namespace = cacheConfig == null ? annotation.namespace() : cacheConfig.namespace();
        // Dependent cache space
        String[] dependOnNamespace = addAll(cacheConfig == null ? null : cacheConfig.dependsOn(), annotation.dependsOn());
        // Clear namespace based on cache implementation
        ICache<Object, Object> cacheProvider = this.cacheProviderMap.get(annotation.implement());
        // Cache Key
        String cacheKey = annotation.key();
        // 默认策略清除所有缓存实现的命名空间
        if (annotation.strategy()) {
            this.cacheProviderMap.forEach((relation, cache) -> {
                // 清除缓存空间
                cache.invalidate(namespace);
                // 清除依赖缓存空间
                for (String depend : dependOnNamespace) {
                    cache.invalidate(depend);
                }
            });
        } else {
            // 不存在key，清除缓存空间
            if (StringUtil.isEmpty(cacheKey)) {
                // 清除缓存空间
                cacheProvider.invalidate(namespace);
                // 清除依赖缓存空间
                for (String depend : dependOnNamespace) {
                    cacheProvider.invalidate(depend);
                }
            } else {
                cacheKey = this.extractCacheKey(method, joinPoint.getArgs(), cacheKey);
                cacheProvider.invalidate(namespace, cacheKey);
            }
        }
    }

    private String extractCacheKey(Method method, Object[] arguments, String springEl) {
        String parse = this.handler.parse(method, arguments, springEl, String.class, springEl);
        return DigestUtils.md5DigestAsHex(parse.getBytes(StandardCharsets.UTF_8));
    }

    private void checkStatus() {
        Assert.notNull(this.handler, "expression handler Uninitialized!");
        Assert.notNull(this.cacheProviderMap, "cache provider Uninitialized!");
    }

    public void addProvider(CacheImplement relation, ICache<Object, Object> cacheProvider) {
        if (this.cacheProviderMap == null) {
            this.cacheProviderMap = new ConcurrentHashMap<>(2);
        }
        this.cacheProviderMap.put(relation, cacheProvider);
    }

    private void executeMethodList(ProceedingJoinPoint joinPoint, Class<?> targetClass, List<String> methodList) {
        if (!CollectionUtils.isEmpty(methodList)) {
            for (String methodName : methodList) {
                Method postInvokeMethod = ReflectionUtils.findMethod(targetClass, methodName);
                if (postInvokeMethod != null) {
                    ReflectionUtils.invokeMethod(postInvokeMethod, joinPoint.getThis());
                }
            }
        }
    }

}
