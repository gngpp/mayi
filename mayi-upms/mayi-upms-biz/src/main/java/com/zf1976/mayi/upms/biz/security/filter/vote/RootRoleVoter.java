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

import com.zf1976.mayi.upms.biz.security.Context;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author mac
 * 2021/12/4 星期六 1:25 AM
 */
public class RootRoleVoter implements AccessDecisionVoter<Object> {

    private String rolePrefix = "ROLE_";

    public String getRolePrefix() {
        return this.rolePrefix;
    }

    /**
     * Allows the default role prefix of <code>ROLE_</code> to be overridden. May be set
     * to an empty value, although this is usually not desirable.
     * @param rolePrefix the new prefix
     */
    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return (attribute.getAttribute() != null) && attribute.getAttribute().startsWith(getRolePrefix());
    }

    /**
     * This implementation supports any type of class, because it does not query the
     * presented secure object.
     * @param clazz the secure object
     * @return always <code>true</code>
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
            return ACCESS_DENIED;
        }
        if (Context.isOwner()) {
            return ACCESS_GRANTED;
        }
        int result = ACCESS_ABSTAIN;
        Collection<? extends GrantedAuthority> authorities = extractAuthorities(authentication);
        for (ConfigAttribute attribute : attributes) {
            if (this.supports(attribute)) {
                result = ACCESS_DENIED;
                // Attempt to find a matching granted authority
                for (GrantedAuthority authority : authorities) {
                    if (attribute.getAttribute().equals(authority.getAuthority())) {
                        return ACCESS_GRANTED;
                    }
                }
            }
        }
        return result;
    }

    Collection<? extends GrantedAuthority> extractAuthorities(Authentication authentication) {
        return authentication.getAuthorities();
    }
}
