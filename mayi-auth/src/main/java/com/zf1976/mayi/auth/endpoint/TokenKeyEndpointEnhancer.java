package com.zf1976.mayi.auth.endpoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * @author ant
 * Create by Ant on 2021/3/13 8:53 AM
 */
@RestController
public class TokenKeyEndpointEnhancer {

    private final KeyPair keyPair;
    private final Map<String, Object> stringObjectMap;

    public TokenKeyEndpointEnhancer(KeyPair keyPair) {
        this.keyPair = keyPair;
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
    @GetMapping("/oauth/token_key")
    public Map<String, Object> getKey(Principal principal) {
        if (principal == null && (this.keyPair.getPublic() == null)) {
            throw new AccessDeniedException("You need to authenticate to see a shared key");
        } else {
            return this.stringObjectMap;
        }
    }

}
