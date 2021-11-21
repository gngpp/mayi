package com.zf1976.mayi.auth.codec;

import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;

import java.security.interfaces.RSAPublicKey;

/**
 * support verification token revocation status
 *
 * @author mac
 * 2021/11/14 星期日 3:06 下午
 */
public class JwtDecoderEnhancer implements JwtDecoder {

    private final JwtDecoder jwtDecoder;
    private final OAuth2AuthorizationService authorizationService;

    public JwtDecoderEnhancer(RSAPublicKey rsaPublicKey, OAuth2AuthorizationService authorizationService) {
        this(NimbusJwtDecoder.withPublicKey(rsaPublicKey).build(), authorizationService);
    }

    public JwtDecoderEnhancer(JwtDecoder jwtDecoder, OAuth2AuthorizationService authorizationService) {
        this.jwtDecoder = jwtDecoder;
        this.authorizationService = authorizationService;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        final var decode = this.jwtDecoder.decode(token);
        final var oAuth2Authorization = authorizationService.findByToken(token, null);
        if (oAuth2Authorization == null) {
            throw new BadJwtException("invalid token");
        }
        if (!oAuth2Authorization.getAccessToken().isActive()) {
            throw new BadJwtException("invalid token");
        }
        return decode;
    }

}
