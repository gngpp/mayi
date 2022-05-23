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

package com.gngpp.mayi.auth.codec;

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
