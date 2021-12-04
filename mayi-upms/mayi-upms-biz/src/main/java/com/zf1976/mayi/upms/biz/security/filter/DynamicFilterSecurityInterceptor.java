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

package com.zf1976.mayi.upms.biz.security.filter;

import com.zf1976.mayi.common.security.matcher.load.LoadDataSource;
import com.zf1976.mayi.upms.biz.security.filter.datasource.DynamicFilterInvocationSecurityMetadataSource;
import com.zf1976.mayi.upms.biz.security.filter.manager.DynamicAccessDecisionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.Assert;

import javax.servlet.*;
import java.io.IOException;

/**
 * 动态权限安全过滤
 *
 * @author mac
 * @date 2020/12/25
 **/
public class DynamicFilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final DynamicFilterInvocationSecurityMetadataSource dynamicSecurityMetadataSource;
    private final Authentication anonymous = new AnonymousAuthenticationToken("key", "anonymous",
            AuthorityUtils.createAuthorityList("authenticated"));

    public DynamicFilterSecurityInterceptor(LoadDataSource loadDataSource) {
        Assert.notNull(loadDataSource, "loadDataSource cannot been null.");
        this.dynamicSecurityMetadataSource = new DynamicFilterInvocationSecurityMetadataSource(loadDataSource);
        super.setAccessDecisionManager(new DynamicAccessDecisionManager(loadDataSource));
        this.checkState();
    }

    public void checkState() {
        Assert.notNull(this.dynamicSecurityMetadataSource, "dynamicSecurityMetadataSource cannot been null");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        FilterInvocation filterInvocation = new FilterInvocation(servletRequest, servletResponse, filterChain);
        // Call the decide method in Access Decision Manager for authentication operation
        InterceptorStatusToken token = super.beforeInvocation(filterInvocation);
        try {
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
        } finally {
            super.finallyInvocation(token);
        }
        // Go directly to the bottom of the controller-servlet
        super.afterInvocation(token, null);
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.dynamicSecurityMetadataSource;
    }

}
