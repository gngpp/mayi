package com.zf1976.mayi.common.component.cache.aspect;


import com.zf1976.mayi.common.component.cache.ICache;
import com.zf1976.mayi.common.component.cache.annotation.CacheConfig;
import com.zf1976.mayi.common.component.cache.annotation.CacheEvict;
import com.zf1976.mayi.common.component.cache.annotation.CachePut;
import com.zf1976.mayi.common.component.cache.aspect.handler.SpringElExpressionHandler;
import com.zf1976.mayi.common.component.cache.enums.CacheImplement;
import com.zf1976.mayi.common.component.cache.impl.GuavaCacheProvider;
import com.zf1976.mayi.common.component.cache.impl.RedisCacheProvider;
import com.zf1976.mayi.common.component.property.GuavaCacheProperties;
import com.zf1976.mayi.common.core.util.StringUtil;
import com.zf1976.mayi.common.security.support.session.manager.SessionManagement;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author WINDOWS
 */
@Aspect
@Component
public class LoadCacheAspect {

    private final Logger log = LoggerFactory.getLogger("[LoadCacheAspect]");
    private final SpringElExpressionHandler handler = new SpringElExpressionHandler();
    private Map<CacheImplement, ICache<Object, Object>> cacheProviderMap;

    public LoadCacheAspect(RedisTemplate<Object, Object> template, GuavaCacheProperties properties) {
        this.addProvider(CacheImplement.CAFFEINE, new GuavaCacheProvider<>(properties));
        this.addProvider(CacheImplement.REDIS, new RedisCacheProvider<>(properties, template));
        this.checkStatus();
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

    public static <T> T[] clone(T[] array) {
        return array == null ? null : array.clone();
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

    /**
     * 获取被代理真实Class
     *
     * @param proxyObj 代理对象
     * @return {@link Class<>}
     */
    private Class<?> extractTargetClass(Object proxyObj) {
        return AopProxyUtils.ultimateTargetClass(proxyObj);
    }

    /**
     * 查询调用
     *
     * @param joinPoint  切点
     * @param annotation 注解
     * @return {@link Object}
     */
    @Around("@annotation(com.zf1976.mayi.common.component.cache.annotation.CachePut) && @annotation(annotation))")
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
        String key = this.handler.parse(method, joinPoint.getArgs(), annotation.key(), String.class, annotation.key());
        if (annotation.dynamics()) {
            key = key.concat(SessionManagement.getCurrentUsername());
        }
        return this.cacheProviderMap.get(annotation.implement())
                                    .getValueAndSupplier(namespace, key, annotation.expired(), () -> {
                                        try {
                                            return joinPoint.proceed();
                                        } catch (Throwable throwable) {
                                            log.error(throwable.getMessage(), throwable);
                                        }
                                        return null;
                                    });
    }

    /**
     * 更新调用
     *
     * @param joinPoint  切点
     * @param annotation 注解
     * @return {@link Object}
     */
    @Around("@annotation(com.zf1976.mayi.common.component.cache.annotation.CacheEvict) && @annotation(annotation)")
    public Object remove(ProceedingJoinPoint joinPoint, CacheEvict annotation) throws Throwable {
        // Get the proxy class
        Class<?> targetClass = this.extractTargetClass(joinPoint.getThis());
        // Proxy method
        Method method = this.handler.filterMethod(joinPoint);
        // Cache configuration
        CacheConfig classAnnotation = targetClass.getAnnotation(CacheConfig.class);
        // namespaces
        String namespace = classAnnotation == null ? annotation.namespace() : classAnnotation.namespace();
        // Dependent cache space
        String[] dependOnNamespace = addAll(classAnnotation == null ? null : classAnnotation.dependsOn(), annotation.dependsOn());
        // Cache Key
        String key = annotation.key();
        // 根据缓存实现清除命名空间
        ICache<Object, Object> cacheProvider = this.cacheProviderMap.get(annotation.implement());

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
            if (StringUtil.isEmpty(key)) {
                // 清除缓存空间
                cacheProvider.invalidate(namespace);
                // 清除依赖缓存空间
                for (String depend : dependOnNamespace) {
                    cacheProvider.invalidate(depend);
                }
            } else {
                key = this.handler.parse(method, joinPoint.getArgs(), key, String.class, null);
                cacheProvider.invalidate(namespace, key);
            }
        }
        Object proceed = joinPoint.proceed();

        List<String> methodList = Arrays.stream(addAll(annotation.postInvoke(), classAnnotation != null ? classAnnotation.postInvoke() : new String[0]))
                                        .distinct()
                                        .collect(Collectors.toUnmodifiableList());
        // Execute the calling method after clearing the cache
        if (!CollectionUtils.isEmpty(methodList)) {
            for (String methodName : annotation.postInvoke()) {
                Method postInvokeMethod = ReflectionUtils.findMethod(targetClass, methodName);
                if (postInvokeMethod != null) {
                    ReflectionUtils.invokeMethod(postInvokeMethod, joinPoint.getThis());
                }
            }
        }
        return proceed;
    }

}
