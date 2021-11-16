package com.zf1976.mayi.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.power.common.util.Base64Util;
import com.zf1976.mayi.auth.enums.AuthenticationState;
import com.zf1976.mayi.auth.exception.BadCredentialsException;
import com.zf1976.mayi.auth.filter.handler.success.OAuth2AuthenticationFailureHandler;
import com.zf1976.mayi.auth.filter.handler.success.OAuth2AuthenticationSuccessHandler;
import com.zf1976.mayi.auth.filter.manager.AuthenticationProviderManager;
import com.zf1976.mayi.common.encrypt.EncryptUtil;
import com.zf1976.mayi.common.security.pojo.dto.AuthenticationDTO;
import com.zf1976.mayi.common.security.pojo.dto.LoginDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.ObjectUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.stream.Collectors;

/**
 * 自定义校验器
 *
 * @author ant
 * Create by Ant on 2020/9/10 6:12 下午
 */
@Deprecated
public class LoginAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    public static final Logger LOG = LoggerFactory.getLogger(LoginAuthenticationProcessingFilter.class);
    private static final String REQUEST_METHOD = "POST";
    private final  ObjectMapper jsonMapper = new ObjectMapper();

    public LoginAuthenticationProcessingFilter() {
        super("/oauth/token");
        this.setAuthenticationManager(new AuthenticationProviderManager());
        this.setAuthenticationSuccessHandler(new OAuth2AuthenticationSuccessHandler());
        this.setAuthenticationFailureHandler(new OAuth2AuthenticationFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!REQUEST_METHOD.equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        UsernamePasswordAuthenticationToken authenticationToken = null;
        final AuthenticationDTO dto = this.parseAuthentication(request);
        if (checkObjectFieldIsNonNull(dto)) {
            LoginDTO details = new LoginDTO();
            String username = dto.getUsername().trim();
            String uuid = dto.getUuid();
            BeanUtils.copyProperties(dto, details);
            authenticationToken = new UsernamePasswordAuthenticationToken(username, uuid);
            authenticationToken.setDetails(details);
        } else {
            this.handlerCredentialsException(request, response,new BadCredentialsException(AuthenticationState.BAD_CREDENTIALS));
        }

        return this.getAuthenticationManager()
                   .authenticate(authenticationToken);
    }

    private AuthenticationDTO parseAuthentication(HttpServletRequest request) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream());
        String base64Content = new BufferedReader(inputStreamReader).lines()
                                                                    .collect(Collectors.joining(System.lineSeparator()));
        byte[] decryptBase64 = Base64Util.decryptBASE64(base64Content);
        String result = EncryptUtil.decryptForAesByCbc(decryptBase64);
        return this.jsonMapper.readValue(result, AuthenticationDTO.class);
    }

    /**
     * 校验必要字段是否为空
     *
     * @param obj object
     * @return boolean
     */
    public static boolean checkObjectFieldIsNonNull(Object obj) {

        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (ObjectUtils.isEmpty(field.get(obj))) {
                    return false;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 认证失败处理
     *
     * @param request  request
     * @param response response
     * @param e        异常
     * @throws IOException      向上抛异常
     * @throws ServletException 向上抛异常
     */
    private void handlerCredentialsException(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        this.getFailureHandler()
            .onAuthenticationFailure(request, response, e);
    }
}
