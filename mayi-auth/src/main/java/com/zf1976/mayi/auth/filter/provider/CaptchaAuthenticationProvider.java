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
