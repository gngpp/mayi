package com.zf1976.mayi.auth.endpoint;

import com.zf1976.mayi.auth.AuthorizationResult;
import com.zf1976.mayi.auth.SecurityContextHolder;
import com.zf1976.mayi.common.component.validate.service.CaptchaService;
import com.zf1976.mayi.common.component.validate.support.CaptchaGenerator;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.security.pojo.Captcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * authentication endpoint
 *
 * @author mac
 * Create by Ant on 2020/9/1 下午11:14
 */
@RestController
@RequestMapping("/oauth")
public class TokenEndpointEnhancer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final AlternativeJdkIdGenerator ALTERNATIVE_JDK_ID_GENERATOR = new AlternativeJdkIdGenerator();
    private final CaptchaService captchaService;
    private final TokenEndpoint tokenEndpoint;

    public TokenEndpointEnhancer(CaptchaService captchaService, TokenEndpoint tokenEndpoint) {
        this.captchaService = captchaService;
        this.tokenEndpoint = tokenEndpoint;
    }

    /**
     * reject get request
     *
     * @param principal  auth principal
     * @param parameters param
     * @return {@link DataResult<OAuth2AccessToken>}
     */
    @GetMapping("/token")
    public DataResult<OAuth2AccessToken> getAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        throw new HttpRequestMethodNotSupportedException("GET");
    }


    @PostMapping("/token")
    public DataResult<AuthorizationResult> postAccessToken(Principal principal,
                                                           @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        if (!(principal instanceof Authentication)) {
            throw new InsufficientAuthenticationException("There is no client authentication. Try adding an appropriate authentication filter.");
        } else {
            ResponseEntity<OAuth2AccessToken> responseEntity = this.tokenEndpoint.postAccessToken(principal, parameters);
            OAuth2AccessToken oAuth2AccessToken = responseEntity.getBody();
            if (responseEntity.getStatusCode().is2xxSuccessful() && oAuth2AccessToken != null) {
                return DataResult.success(new AuthorizationResult(oAuth2AccessToken, SecurityContextHolder.getAuthorizationUser()));
            }
            throw new InsufficientAuthenticationException("Client authentication failed.");
        }
    }

    /**
     * get verification code
     *
     * @return @{@link ResponseEntity<Captcha>}
     */
    @GetMapping("/code")
    public ResponseEntity<Captcha> getVerifyCode() {
        // get verification code
        com.wf.captcha.base.Captcha captcha = CaptchaGenerator.getCaptcha();
        // generate uuid
        UUID uuid = ALTERNATIVE_JDK_ID_GENERATOR.generateId();
        // save the verification code in the redis cache
        boolean isSave = captchaService.storeCaptcha(uuid.toString(), captcha.text());
        if (isSave) {
            if (logger.isDebugEnabled()) {
                logger.info("Generator Captcha is：" + captcha.text());
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.info("Captcha：{} not saved.", captcha.text());
            }
        }
        return ResponseEntity.ok(new Captcha(uuid.toString(), captcha.toBase64()));
    }

}
