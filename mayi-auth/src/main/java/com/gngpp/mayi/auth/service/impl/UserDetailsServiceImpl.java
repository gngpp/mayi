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

package com.gngpp.mayi.auth.service.impl;

import com.gngpp.mayi.auth.Context;
import com.gngpp.mayi.auth.core.AuthorizationUserDetails;
import com.gngpp.mayi.auth.enums.AuthenticationState;
import com.gngpp.mayi.common.core.foundation.DataResult;
import com.gngpp.mayi.upms.biz.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * @author mac
 * Create by Ant on 2020/9/2 下午7:02
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Override
    public UserDetails loadUserByUsername(String username) {
        DataResult<User> dataResult;
        try {
            dataResult = Context.loadUserByUsername(username);
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            throw new InternalAuthenticationServiceException("authorization server error");
        }
        User user = dataResult.getData();
        if (user == null) {
            throw new UsernameNotFoundException(AuthenticationState.USER_NOT_FOUNT.getReasonPhrase());
        }
        final var permissions = String.join(",", user.getPermissions());
        return AuthorizationUserDetails.builder()
                                       .delegateUser(user)
                                       .username(user.getUsername())
                                       .password(user.getPassword())
                                       .authorities(AuthorityUtils.commaSeparatedStringToAuthorityList(permissions))
                                       .accountLocked(!user.getEnabled())
                                       .disabled(!user.getEnabled())
                                       .build();
    }

}
