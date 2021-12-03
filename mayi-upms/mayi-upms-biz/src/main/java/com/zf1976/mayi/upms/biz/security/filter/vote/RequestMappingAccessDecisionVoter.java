package com.zf1976.mayi.upms.biz.security.filter.vote;

import com.zf1976.mayi.common.security.matcher.RequestMatcher;
import com.zf1976.mayi.common.security.matcher.load.LoadDataSource;
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
        // blacklist request
        final var loadBlackListRequest = this.loadDataSource.loadBlackListRequest();
        if (CollectionUtils.isEmpty(loadBlackListRequest)) {
            return ACCESS_ABSTAIN;
        }

        FilterInvocation filterInvocation = (FilterInvocation) object;
        HttpServletRequest request = filterInvocation.getRequest();
        // matching blocklist request
        for (RequestMatcher requestMatcher : loadBlackListRequest) {
            if (requestMatcher.matches(request)) {
                return ACCESS_DENIED;
            }
        }

        return ACCESS_GRANTED;
    }


}
