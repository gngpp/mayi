package com.zf1976.mayi.auth.filter.provider;

import com.zf1976.mayi.auth.enums.AuthenticationState;
import com.zf1976.mayi.auth.exception.CaptchaException;
import com.zf1976.mayi.common.component.validate.service.CaptchaService;
import com.zf1976.mayi.common.core.util.SpringContextHolder;
import com.zf1976.mayi.common.security.pojo.dto.LoginDTO;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * @author ant
 * Create by Ant on 2020/9/12 12:33 下午
 */
@Deprecated
public class CaptchaAuthenticationProvider implements AuthenticationProvider {

    private final CaptchaService verifyCodeService = SpringContextHolder.getBean(CaptchaService.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws CaptchaException {
        LoginDTO details = (LoginDTO) authentication.getDetails();
        String password = details.getPassword();
        String uuid = (String) authentication.getCredentials();
        UsernamePasswordAuthenticationToken authenticationToken;
        if (verifyCodeService.validateCaptcha(uuid, details.getCode())) {
            verifyCodeService.clearCaptcha(uuid);
            authenticationToken = new UsernamePasswordAuthenticationToken(authentication.getName(), password);
            authenticationToken.setDetails(details);
            return authenticationToken;
        }
        throw new CaptchaException(AuthenticationState.CAPTCHA_FAIL);
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
