package com.zf1976.mayi.common.component.cache;

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 使用Redisson分布式锁
 *
 * @author mac
 * 2021/10/8 星期五 3:45 下午
 */
public abstract class AbstractCacheAsideLock {

    protected final ReentrantLock lock = new ReentrantLock(true);
    protected final static int DEFAULT_LOCK_TIME = (1 << 5) - 2;

    protected AbstractCacheAsideLock() {}

    protected Object doLockAndUpdate(ProceedingJoinPoint joinPoint, Consumer<Void> beforeHandler, Consumer<Void> afterHandler) throws Throwable {
        // clear cache
        beforeHandler.accept(null);
        boolean isException = true;
        lock.lock();
        try {
            Object proceed = joinPoint.proceed();
            isException = false;
            return proceed;
        } finally {
            lock.unlock();
            if (!isException) {
                // Execute the calling method after clearing the cache
                afterHandler.accept(null);
            }
        }
    };

    protected Object doLockAndPut(ProceedingJoinPoint joinPoint, Supplier<Object> supplier) throws Throwable{
        return null;
    }
}
