package com.zf1976.mayi.auth.grant;

//import com.zf1976.mayi.common.component.validate.service.CaptchaService;
//import org.springframework.security.authentication.*;
//import org.springframework.security.core.Authentication;
//
//import java.util.LinkedHashMap;
//
///**
// * 密码模式增强，新增验证码
// * @author mac
// * @date 2021/3/25
// **/
//public class ResourceOwnerPasswordTokenEnhancerGranter extends AbstractTokenGranter {
//    private static final String GRANT_TYPE = "password_code";
//    private final AuthenticationManager authenticationManager;
//    private final CaptchaService captchaService;
//
//    public ResourceOwnerPasswordTokenEnhancerGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, CaptchaService captchaService) {
//        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
//        this.authenticationManager = authenticationManager;
//        this.captchaService = captchaService;
//    }
//
//    @Override
//    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
//        var parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
//        var username = parameters.get("username");
//        var password = parameters.get("password");
//        var verifyCode = parameters.get("code");
//        var uuid = parameters.get("uuid");
//        parameters.remove("password");
//        parameters.remove("code");
//        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
//        ((AbstractAuthenticationToken)userAuth).setDetails(parameters);
//
//        // 无效验证码
//        if (!this.verifyCodeAuthentication(uuid, verifyCode)) {
//            throw new InvalidGrantException("Could not authenticate verify code: " + verifyCode);
//        }
//
//        // 认证账号
//        try {
//            userAuth = this.authenticationManager.authenticate(userAuth);
//        } catch (AccountStatusException | BadCredentialsException var8) {
//            throw new InvalidGrantException(var8.getMessage());
//        }
//
//        // 是否认证成功
//        if (userAuth != null && userAuth.isAuthenticated()) {
//            var storedOAuth2Request = this.getRequestFactory().createOAuth2Request(client, tokenRequest);
//            return new OAuth2Authentication(storedOAuth2Request, userAuth);
//        } else {
//            throw new InvalidGrantException("Could not authenticate user: " + username);
//        }
//    }
//
//    private boolean verifyCodeAuthentication(String uuid, String verifyCode) {
//        return captchaService.validateCaptcha(uuid, verifyCode);
//    }
//}
