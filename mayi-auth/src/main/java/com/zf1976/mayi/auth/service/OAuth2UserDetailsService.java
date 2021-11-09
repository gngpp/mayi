package com.zf1976.mayi.auth.service;

import com.zf1976.mayi.auth.LoginUserDetails;
import com.zf1976.mayi.auth.exception.UserNotFountException;
import com.zf1976.mayi.auth.feign.RemoteUserService;
import com.zf1976.mayi.common.core.constants.SecurityConstants;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.security.enums.AuthenticationState;
import com.zf1976.mayi.upms.biz.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


/**
 * @author mac
 * Create by Ant on 2020/9/2 下午7:02
 */
@Service("userDetailsService")
public class OAuth2UserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private final RemoteUserService remoteUserService;

    public OAuth2UserDetailsService(RemoteUserService remoteUserService) {
        this.remoteUserService = remoteUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        DataResult<User> userDataResult;
        try {
            userDataResult = this.remoteUserService.getUser(username, SecurityConstants.FROM_IN);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AuthenticationServiceException("server error");
        }
        User user = userDataResult.getData();
        if (user == null) {
            throw new UserNotFountException(AuthenticationState.USER_NOT_FOUNT);
        }
        if (!user.getEnabled()) {
            throw new LockedException(this.messages.getMessage("AccountStatusUserDetailsChecker.locked", "User account is locked"));
        }
        return new LoginUserDetails(user);
    }

}
