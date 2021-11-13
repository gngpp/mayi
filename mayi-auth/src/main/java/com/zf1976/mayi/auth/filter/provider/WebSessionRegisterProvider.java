package com.zf1976.mayi.auth.filter.provider;

import com.zf1976.mayi.auth.deprecate.JwtTokenProvider;
import com.zf1976.mayi.auth.service.AuthorizationUserDetails;
import com.zf1976.mayi.common.core.util.SpringContextHolder;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.common.security.support.session.manager.SessionManagement;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author mac
 * Create by Ant on 2020/10/3 15:09
 */
@Deprecated
public class WebSessionRegisterProvider implements AuthenticationProvider {

    private static final SecurityProperties CONFIG = SpringContextHolder.getBean(SecurityProperties.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String token = JwtTokenProvider.createToken(authentication);
        final AuthorizationUserDetails userDetails = (AuthorizationUserDetails) authentication.getDetails();
        //校验登陆状态 踢下线
        if (CONFIG.getTokenSingle()) {
            SessionManagement.removeSession(userDetails.getId());
        }
        UsernamePasswordAuthenticationToken authenticationToken;
        authenticationToken = new UsernamePasswordAuthenticationToken(authentication.getName(),
                                                                      token,
                                                                      authentication.getAuthorities());
        authenticationToken.setDetails(userDetails);
        return authenticationToken;
    }

    /**
     * 表明这provider可以处理身份验证请求。因为目前只有一个登录,总是返回true
     *
     * @param aClass 类对象
     * @return boolean
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
