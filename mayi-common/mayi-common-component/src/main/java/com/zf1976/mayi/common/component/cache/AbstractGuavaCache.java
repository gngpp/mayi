package com.zf1976.mayi.common.component.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zf1976.mayi.common.component.property.GuavaCacheProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author WINDOWS
 */
public abstract class AbstractGuavaCache<K, V> implements ICache<K, V> {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractGuavaCache.class);
    protected static final byte MAP_INITIAL_CAPACITY = 16;
    protected Map<String, Cache<K, V>> cacheSpace;
    protected volatile Cache<K, V> kvCache;
    protected final GuavaCacheProperties properties;

    public AbstractGuavaCache(GuavaCacheProperties properties) {
        this.properties = properties;
    }

    /**
     * 初始化
     */
    protected void initialCache() {
        /*
         * 缓存过期时间默认十分钟
         */
        this.kvCache = this.loadCache(600L);
        this.cacheSpace = new ConcurrentHashMap<>(16);
    }

    /**
     * 加载缓存对象
     *
     * @return cache object
     */
    protected Cache<K, V> loadCache(Long expired) {
        Assert.notNull(expired,"expired time cannot been null");
        return CacheBuilder.newBuilder()
                           .recordStats()
                           .concurrencyLevel(properties.getConcurrencyLevel())
                           .initialCapacity(properties.getInitialCapacity())
                           .maximumSize(properties.getMaximumSize())
                           .expireAfterWrite(expired, TimeUnit.SECONDS)
                           .removalListener(removalNotification -> {
                               LOG.info(removalNotification.getKey() + " " + removalNotification.getValue() + " is remove!");
                           })
                           .build();
    }


    protected Cache<K, V> getObject() {
        // 继承子类没有给予初始化 则提供默认初始化
        if (kvCache == null) {
            synchronized (AbstractGuavaCache.class) {
                if (kvCache == null) {
                    initialCache();
                }
            }
        }
        return this.kvCache;
    }


    @Override
    public String formatNamespace(String key) {
        return this.properties.getKeyPrefix().concat(key);
    }

}
