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

package com.zf1976.mayi.auth;

import com.zf1976.mayi.auth.feign.RemoteUserService;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.upms.biz.pojo.User;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * 只适合单机适用
 *
 * @author mac
 */
@Component
@RefreshScope
public class Context extends SecurityContextHolder {

    private static final Map<Class<?>, Object> CONTENTS_MAP = new ConcurrentReferenceHashMap<>(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);
    private static SecurityProperties securityProperties;
    private static RemoteUserService remoteUserService;

    public Context(SecurityProperties securityProperties, RemoteUserService remoteUserService) {
        Context.securityProperties = securityProperties;
        Context.remoteUserService = remoteUserService;
    }

    public static void setShareObject(Class<?> clazz, Object object) {
        Assert.isInstanceOf(clazz, object, "must be an instance of class");
        CONTENTS_MAP.put(clazz, object);
    }

    public static <T> T getShareObject(Class<T> clazz) {
        final var cast = clazz.cast(CONTENTS_MAP.get(clazz));
        CONTENTS_MAP.remove(clazz);
        return cast;
    }

    public static boolean isOwner(String username) {
        return ObjectUtils.nullSafeEquals(Context.securityProperties.getOwner(), username);
    }

    public static boolean isOwner() {
        String name = username();
        return isOwner(name);
    }

    public static String username() {
        return getContext().getAuthentication().getName();
    }

    private static String communicationToken() {
        return Context.securityProperties.getCommunicationToken();
    }

    public static DataResult<User> loadUserByUsername(String username) {
        return remoteUserService.findUserByUsername(Context.communicationToken(), username);
    }

}
