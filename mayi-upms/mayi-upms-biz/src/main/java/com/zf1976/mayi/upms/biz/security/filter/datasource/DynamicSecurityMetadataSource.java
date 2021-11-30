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

package com.zf1976.mayi.upms.biz.security.filter.datasource;

import com.zf1976.mayi.upms.biz.security.service.DynamicDataSourceService;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * 动态权限数据源
 *
 * @author mac
 * @date 2020/12/25
 **/
public class DynamicSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private final DynamicDataSourceService dynamicDataSourceService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public DynamicSecurityMetadataSource(DynamicDataSourceService dynamicDataSourceService) {
        this.dynamicDataSourceService = dynamicDataSourceService;
        this.checkState();
    }

    private void checkState() {
        Assert.notNull(this.dynamicDataSourceService, "dynamicDataSourceService cannot been null");
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        Assert.isInstanceOf(FilterInvocation.class, o, "target not is instance of FilterInvocation");
        HttpServletRequest request = ((FilterInvocation) o).getRequest();
        String uri = request.getRequestURI();
        // 资源URI--Permissions
        Set<Map.Entry<String, String>> entrySet = this.loadDynamicDataSource().entrySet();
        // 权限匹配
        for (Map.Entry<String, String> entry : entrySet) {
            // eq匹配成功退出
            if (ObjectUtils.nullSafeEquals(entry.getKey(), uri)) {
                return this.toAttribute(entry.getValue());
                // 模式匹配成功退出
            } else if (pathMatcher.match(entry.getKey(), uri)) {
                return this.toAttribute(entry.getValue());
            }
        }
        // 不存在资源资源路径 返回空
        return Collections.emptyList();
    }

    /**
     * 获取所有权限属性
     *
     * @return {@link Collection<ConfigAttribute>}
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Collection<ConfigAttribute> configAttributes = new CopyOnWriteArraySet<>();
        for (Map.Entry<String, String> entry : this.loadDynamicDataSource().entrySet()) {
            final List<ConfigAttribute> securityConfigs = toAttribute(entry.getValue());
            configAttributes.addAll(securityConfigs);
        }
        return configAttributes;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return DynamicSecurityMetadataSource.class.isAssignableFrom(aClass);
    }

    private List<ConfigAttribute> toAttribute(String value) {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(value)
                             .stream()
                             .filter(Objects::nonNull)
                             .map(GrantedAuthority::getAuthority)
                             .map(SecurityConfig::new)
                             .collect(Collectors.toList());
    }
    private Map<String, String> loadDynamicDataSource() {
        return this.dynamicDataSourceService.loadDynamicDataSource();
    }
}
