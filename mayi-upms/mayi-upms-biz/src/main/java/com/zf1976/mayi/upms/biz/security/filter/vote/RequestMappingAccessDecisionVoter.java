package com.zf1976.mayi.upms.biz.security.filter.vote;

import com.zf1976.mayi.upms.biz.security.service.DynamicDataSourceService;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;

/**
 * @author mac
 * 2021/11/29 星期一 3:29 下午
 */
public class RequestMappingAccessDecisionVoter implements AccessDecisionVoter<Object> {

    private final DynamicDataSourceService dynamicDataSourceService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public RequestMappingAccessDecisionVoter(DynamicDataSourceService dynamicDataSourceService) {
        this.dynamicDataSourceService = dynamicDataSourceService;
    }

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
        // uri-method
        Map<String, String> resourceMethodMap = this.loadDynamicDataSource();
        if (CollectionUtils.isEmpty(resourceMethodMap)) {
            return ACCESS_ABSTAIN;
        }

        FilterInvocation filterInvocation = (FilterInvocation) object;
        HttpServletRequest request = filterInvocation.getRequest();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // matching resource method
        for (Map.Entry<String, String> entry : resourceMethodMap.entrySet()) {
            // eq matching
            if (ObjectUtils.nullSafeEquals(entry.getKey(), uri)) {
                if (!ObjectUtils.nullSafeEquals(entry.getValue(), method)) {
                    throw new AccessDeniedException("Resource does not support request the method：" + method);
                }
                return ACCESS_GRANTED;
                // pattern matching
            } else if (pathMatcher.match(entry.getKey(), uri)) {
                if (!ObjectUtils.nullSafeEquals(entry.getValue(), method)) {
                    throw new AccessDeniedException("Resource does not support request the method：" + method);
                }
                return ACCESS_GRANTED;
            }
        }

        return ACCESS_DENIED;
    }


    private Map<String, String> loadDynamicDataSource() {
        return this.dynamicDataSourceService.loadDynamicPermissionDataSource();
    }

    private Map<String, String> loadBlacklistDynamicDataSource() {
        return this.dynamicDataSourceService.loadDynamicPermissionDataSource();
    }

}
