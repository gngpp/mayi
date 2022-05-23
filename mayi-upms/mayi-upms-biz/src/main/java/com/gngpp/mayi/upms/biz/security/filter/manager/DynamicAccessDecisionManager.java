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

package com.gngpp.mayi.upms.biz.security.filter.manager;

import com.gngpp.mayi.common.security.matcher.load.LoadDataSource;
import com.gngpp.mayi.upms.biz.security.filter.vote.AuthoritiesAccessDecisionVoter;
import com.gngpp.mayi.upms.biz.security.filter.vote.RequestMappingAccessDecisionVoter;
import com.gngpp.mayi.upms.biz.security.filter.vote.RootRoleVoter;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.List;

/**
 * 动态访问决策管理器, 默认使用一票否决投票
 *
 * @author mac
 * @date 2020/12/25
 **/
public class DynamicAccessDecisionManager extends AbstractAccessDecisionManager {

    public DynamicAccessDecisionManager(LoadDataSource LoadDataSource) {
        super(List.of(
                new RootRoleVoter(),
                new RequestMappingAccessDecisionVoter(LoadDataSource),
                new AuthoritiesAccessDecisionVoter()));
        setAllowIfAllAbstainDecisions(true);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) throws AccessDeniedException, InsufficientAuthenticationException {
        int needGrant = getDecisionVoters().size();
        int grant = 0;
        go:
        for (AccessDecisionVoter voter : getDecisionVoters()) {
            int result = voter.vote(authentication, object, attributes);
            switch (result) {
                case AccessDecisionVoter.ACCESS_GRANTED -> {
                    break go;
                }
                case AccessDecisionVoter.ACCESS_DENIED -> throw new AccessDeniedException(
                        this.messages.getMessage("AbstractAccessDecisionManager.accessDenied", "Access is denied"));
                default -> grant++;
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
