package com.zf1976.mayi.upms.biz.security.filter.vote;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @author mac
 * 2021/11/29 星期一 3:21 下午
 */
public class AuthoritiesAccessDecisionVoter implements AccessDecisionVoter<Object> {

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return attribute != null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
            return ACCESS_DENIED;
        }

        // The required permission is empty, let it go immediately
        if (CollectionUtils.isEmpty(attributes)) {
            return ACCESS_ABSTAIN;
        }

        int needVoter = attributes.size();
        int hasVoter = 0;
        // User all permissions
        Collection<? extends GrantedAuthority> hasAuthorities = extractAuthorities(authentication);
        for (ConfigAttribute attribute : attributes) {
            if (this.supports(attribute)) {
                // Attempt to find a matching granted authority
                for (GrantedAuthority grantedAuthority : hasAuthorities) {
                    if (attribute.getAttribute().equals(grantedAuthority.getAuthority())) {
                        ++hasVoter;
                        break;
                    }
                }
            }
        }

        if (hasVoter < needVoter) {
            return ACCESS_DENIED;
        }
        return ACCESS_GRANTED;
    }

    Collection<? extends GrantedAuthority> extractAuthorities(Authentication authentication) {
        return authentication.getAuthorities();
    }
}
