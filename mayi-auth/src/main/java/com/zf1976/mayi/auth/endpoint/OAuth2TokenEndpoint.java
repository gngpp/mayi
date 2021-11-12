package com.zf1976.mayi.auth.endpoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.zf1976.mayi.common.component.validate.service.CaptchaService;
import com.zf1976.mayi.common.component.validate.support.CaptchaGenerator;
import com.zf1976.mayi.common.security.pojo.Captcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.UUID;

/**
 * @author ant
 * Create by Ant on 2021/3/13 8:53 AM
 */
@RestController
@RequestMapping("/oauth2")
public class OAuth2TokenEndpoint {

    private final KeyPair keyPair;
    private final Map<String, Object> stringObjectMap;
    private static final AlternativeJdkIdGenerator ALTERNATIVE_JDK_ID_GENERATOR = new AlternativeJdkIdGenerator();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CaptchaService captchaService;

    public OAuth2TokenEndpoint(KeyPair keyPair, CaptchaService captchaService) {
        this.keyPair = keyPair;
        this.captchaService = captchaService;
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey rsaKey = new RSAKey.Builder(publicKey).build();
        this.stringObjectMap = new JWKSet(rsaKey).toJSONObject();
    }

    /**
     * 获取公钥
     *
     * @param principal auth principal
     * @return {@link java.io.Serializable}
     */
    @GetMapping("/token_key")
    public Map<String, Object> getKey(Principal principal) {
        if (principal == null && (this.keyPair.getPublic() == null)) {
            throw new AccessDeniedException("You need to authenticate to see a shared key");
        } else {
            return this.stringObjectMap;
        }
    }

    /**
     * get verification code
     *
     * @return @{@link ResponseEntity < Captcha >}
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
