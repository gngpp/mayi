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

package com.zf1976.mayi.common.component.validate.repository.impl;

import com.zf1976.mayi.common.component.validate.repository.CaptchaRepository;
import com.zf1976.mayi.common.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * Create by Ant on 2020/9/1 下午2:11
 */
public class RedisCaptchaRepository implements CaptchaRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final StringRedisTemplate stringRedisTemplate;

    public RedisCaptchaRepository(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.checkState();
    }

    private void checkState() {
        Assert.notNull(this.stringRedisTemplate, "redis repository cannot be null");
    }

    @Override
    public void store(String key, String value, Long expire, TimeUnit timeUnit) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, expire, timeUnit);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception.getCause());
            throw exception;
        }
    }

    @Override
    public String read(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e.getCause());
            return StringUtil.ENMPTY;
        }
    }

    @Override
    public void remove(String key) {
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception.getCause());
        }
    }

    @Override
    public boolean isAvailable() {
        try {
            return this.stringRedisTemplate.getConnectionFactory() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
