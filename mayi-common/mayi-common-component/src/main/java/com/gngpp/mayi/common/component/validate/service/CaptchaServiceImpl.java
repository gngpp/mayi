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

package com.gngpp.mayi.common.component.validate.service;

import com.gngpp.mayi.common.component.property.CaptchaProperties;
import com.gngpp.mayi.common.component.validate.repository.CaptchaRepository;
import com.gngpp.mayi.common.component.validate.repository.impl.RedisCaptchaRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

/**
 * 提醒这里有个Mybatis Plus 跟springboot的坑
 * Application Context 上下文为初始化完前
 *
 * @author mac
 * Create by Ant on 2020/9/1 下午2:10
 */
@Service("captchaService")
public class CaptchaServiceImpl implements CaptchaService {

    private final CaptchaProperties properties;
    private final CaptchaRepository repository;

    public CaptchaServiceImpl(CaptchaProperties properties, StringRedisTemplate stringRedisTemplate) {
        this.properties = properties;
        this.repository = new RedisCaptchaRepository(stringRedisTemplate);
    }

    @Override
    public boolean storeCaptcha(String key, String value) {
        if (this.repository.isAvailable()) {
            try {
                repository.store(this.keyFormatter(key), value, properties.getExpiration(), TimeUnit.MINUTES);
                return true;
            } catch (Exception ignored) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean validateCaptcha(String key, String code) {
        if (this.repository.isAvailable()) {
            final String storeCode = repository.read(this.keyFormatter(key));
            this.repository.remove(this.keyFormatter(key));
            return ObjectUtils.nullSafeEquals(storeCode, code);
        }
        return false;
    }

    @Override
    public void clearCaptcha(String key) {
        this.repository.remove(this.keyFormatter(key));
    }

    private String keyFormatter(String key) {
        return this.properties.getKeyPrefix() + key;
    }

}
