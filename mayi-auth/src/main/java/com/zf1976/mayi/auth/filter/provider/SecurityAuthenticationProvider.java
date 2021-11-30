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
import com.zf1976.mayi.auth.exception.PasswordException;
import com.zf1976.mayi.auth.exception.RsaDecryptException;
import com.zf1976.mayi.common.core.util.SpringContextHolder;
import com.zf1976.mayi.common.encrypt.EncryptUtil;
import com.zf1976.mayi.common.security.pojo.dto.LoginDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

/**
 * 自定义认证处理器
 *
 * @author ant
 * Create by Ant on 2020/9/12 10:00 上午
 */
@Deprecated
public class SecurityAuthenticationProvider implements AuthenticationProvider {

    public static final Logger LOG = LoggerFactory.getLogger(SecurityAuthenticationProvider.class);

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    public SecurityAuthenticationProvider() {
        this.userDetailsService = SpringContextHolder.getBean(UserDetailsService.class);
        this.passwordEncoder = SpringContextHolder.getBean(BCryptPasswordEncoder.class);
    }

    @Override
    public Authentication authenticate(Authentication authentication){
        UserDetails userDetails = userDetailsService.loadUserByUsername((String) authentication.getPrincipal());
        String encodePassword = userDetails.getPassword();
        String rawPassword = parserPassword((String) authentication.getCredentials());
        UsernamePasswordAuthenticationToken authenticationToken;
        // 用户校验通过
        if (matchesPassword(rawPassword, encodePassword)) {
            final LoginDTO credentials = (LoginDTO) authentication.getDetails();
            authenticationToken = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
                                                                          credentials.getUuid(),
                                                                          userDetails.getAuthorities());
            authenticationToken.setDetails(userDetails);
            return authenticationToken;
        } else {
            throw new PasswordException(AuthenticationState.USERNAME_PASSWORD_ERROR);
        }
    }

    /**
     * 解密
     *
     * @param encodePass 经过rsa加密
     * @return /
     */
    public String parserPassword(String encodePass) {
        if (!StringUtils.isEmpty(encodePass)) {
            try {
                return EncryptUtil.decryptForRsaByPrivateKey(encodePass);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                throw new RsaDecryptException(AuthenticationState.RSA_DECRYPT);
            }
        }
        return encodePass;
    }

    /**
     * 密码匹配
     *
     * @param rawPassword    原始密码
     * @param encodePassword 编码密码
     * @return boolean
     */
    private boolean matchesPassword(String rawPassword, String encodePassword) {
        return passwordEncoder.matches(rawPassword, encodePassword);
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
