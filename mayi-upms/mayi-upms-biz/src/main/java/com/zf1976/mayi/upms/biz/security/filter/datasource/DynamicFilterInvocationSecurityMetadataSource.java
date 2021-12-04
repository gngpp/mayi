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

package com.zf1976.mayi.upms.biz.security.filter.datasource;

import com.zf1976.mayi.common.security.matcher.RequestMatcher;
import com.zf1976.mayi.common.security.matcher.load.LoadDataSource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 动态权限数据源
 *
 * @author mac
 * @date 2020/12/25
 **/
public record DynamicFilterInvocationSecurityMetadataSource(
        LoadDataSource loadDataSource) implements FilterInvocationSecurityMetadataSource {

    public DynamicFilterInvocationSecurityMetadataSource(LoadDataSource loadDataSource) {
        this.loadDataSource = loadDataSource;
        this.checkState();
    }

    private void checkState() {
        Assert.notNull(this.loadDataSource, "loadDataSource cannot been null");
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        Assert.isInstanceOf(FilterInvocation.class, o, "target not is instance of FilterInvocation");
        HttpServletRequest request = ((FilterInvocation) o).getRequest();
        final var requestMatcherCollectionMap = this.loadDynamicPermissionDataSource();
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMatcherCollectionMap.entrySet()) {
            if (entry.getKey().matches(request)) {
                return entry.getValue();
            }
        }
        // There is no resource path return empty
        return Collections.emptyList();
    }

    /**
     * get all permission attributes
     *
     * @return {@link Collection<ConfigAttribute>}
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return this.loadDynamicPermissionDataSource()
                   .values()
                   .stream()
                   .flatMap(Collection::stream)
                   .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return DynamicFilterInvocationSecurityMetadataSource.class.isAssignableFrom(aClass);
    }

    private Map<RequestMatcher, Collection<ConfigAttribute>> loadDynamicPermissionDataSource() {
        return this.loadDataSource.loadRequestMap();
    }

}
