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

import org.aspectj.lang.ProceedingJoinPoint;

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
