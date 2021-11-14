package com.zf1976.mayi.auth.filter.handler.revoke;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zf1976.mayi.auth.service.AuthorizationUserDetails;
import com.zf1976.mayi.common.core.foundation.DataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author mac
 * Create by Ant on 2020/10/4 00:18
 */
public class Oauth2LogoutSuccessHandler implements LogoutSuccessHandler {

    public final Logger log = LoggerFactory.getLogger("log-[Logout]-" + this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpServletResponse.setStatus(HttpStatus.OK.value());
        objectMapper.writeValue(httpServletResponse.getOutputStream(), DataResult.success("Have to log out！"));
        log.info("Authentication principle {} is revoke", this.extractUsername(authentication));
    }

    private String extractUsername(Authentication authentication) {
        AuthorizationUserDetails userDetails = (AuthorizationUserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
