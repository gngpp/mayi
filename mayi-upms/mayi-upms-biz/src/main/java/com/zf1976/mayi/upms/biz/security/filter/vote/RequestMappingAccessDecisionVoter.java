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

package com.zf1976.mayi.upms.biz.security.filter.vote;

import com.zf1976.mayi.common.security.matcher.RequestMatcher;
import com.zf1976.mayi.common.security.matcher.load.LoadDataSource;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * @author mac
 * 2021/11/29 星期一 3:29 下午
 */
public record RequestMappingAccessDecisionVoter(
        LoadDataSource loadDataSource) implements AccessDecisionVoter<Object> {

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        // options request direct release
        if (request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
            return ACCESS_GRANTED;
        }
        // blacklist request
        final var loadBlackListRequest = this.loadDataSource().loadBlackListRequest();
        if (CollectionUtils.isEmpty(loadBlackListRequest)) {
            return ACCESS_ABSTAIN;
        }
        // matching blocklist request
        for (RequestMatcher requestMatcher : loadBlackListRequest) {
            if (requestMatcher.matches(request)) {
                return ACCESS_DENIED;
            }
        }
        final var loadAllowRequest = this.loadDataSource().loadAllowRequest();
        if (CollectionUtils.isEmpty(loadAllowRequest)) {
            return ACCESS_ABSTAIN;
        }
        // whitelist request for direct release
        for (RequestMatcher matcher : loadAllowRequest) {
            if (matcher.matches(request)) {
                return ACCESS_GRANTED;
            }
        }
        return ACCESS_ABSTAIN;
    }

}
