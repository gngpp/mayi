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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @author ant
 * Create by Ant on 2020/8/31 8:03 下午
 */
@Component
@SuppressWarnings("unchecked")
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    private static ApplicationContext applicationContext = null;

    public static <T> T getBean(String name) {
        return (T) Objects.requireNonNull(applicationContext).getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return Objects.requireNonNull(applicationContext).getBean(clazz);
    }

    public static <T> T getProperties(String key) {
        return (T) getProperties(key, null, String.class);
    }

    public static <T> T getProperties(String key, Class<T> requiredType) {
        return getBean(Environment.class).getProperty(key, requiredType);
    }

    public static <T> T getProperties(String key, T defaultValue, Class<T> requiredType) {
        try {
            defaultValue = getBean(Environment.class).getProperty(key, requiredType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public ApplicationContext getApplicationContext() {
        return Objects.requireNonNull(applicationContext);
    }

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        Assert.notNull(applicationContext, "application context cannot be null");
        SpringContextHolder.applicationContext = applicationContext;
    }

    @Override
    public void destroy() {
        applicationContext = null;
    }
}
