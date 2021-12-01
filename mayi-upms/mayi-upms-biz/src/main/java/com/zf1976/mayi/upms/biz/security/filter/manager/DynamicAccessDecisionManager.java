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

package com.zf1976.mayi.upms.biz.security.filter.manager;

import com.zf1976.mayi.upms.biz.security.Context;
import com.zf1976.mayi.upms.biz.security.filter.vote.AuthoritiesAccessDecisionVoter;
import com.zf1976.mayi.upms.biz.security.filter.vote.RequestMappingAccessDecisionVoter;
import com.zf1976.mayi.upms.biz.security.service.DynamicDataSourceService;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.List;

/**
 * 动态访问决策管理器
 *
 * @author mac
 * @date 2020/12/25
 **/
public class DynamicAccessDecisionManager extends AbstractAccessDecisionManager {

    public DynamicAccessDecisionManager(DynamicDataSourceService dynamicDataSourceService) {
        super(List.of(new AuthoritiesAccessDecisionVoter(),
                new RequestMappingAccessDecisionVoter(dynamicDataSourceService)));
        setAllowIfAllAbstainDecisions(true);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) throws AccessDeniedException, InsufficientAuthenticationException {
        if (Context.isOwner()) {
            return;
        }

        int needGrant = getDecisionVoters().size();
        int grant = 0;
        for (AccessDecisionVoter voter : getDecisionVoters()) {
            int result = voter.vote(authentication, object, attributes);
            switch (result) {
                case AccessDecisionVoter.ACCESS_GRANTED -> grant++;
                case AccessDecisionVoter.ACCESS_DENIED -> throw new AccessDeniedException(
                        this.messages.getMessage("AbstractAccessDecisionManager.accessDenied", "Access is denied"));
                default -> {
                }
            }
        }
        if (grant >= 0 && grant <= needGrant) {
            return;
        }
        // To get this far, every AccessDecisionVoter abstained
        checkAllowIfAllAbstainDecisions();
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return DynamicAccessDecisionManager.class.isAssignableFrom(aClass);
    }

}
