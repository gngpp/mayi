/*
 * Copyright (c) 2021 zf1976
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.auth.oauth2.authorization;

import com.zf1976.mayi.auth.Context;
import com.zf1976.mayi.auth.CustomizeOAuth2ParameterNames;
import com.zf1976.mayi.auth.core.AuthorizationUserDetails;
import com.zf1976.mayi.common.core.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class OAuth2JwtTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final Logger log = LoggerFactory.getLogger("[OAuth2TokenCustomize]");

    @Override
    public void customize(JwtEncodingContext context) {
        Authentication authentication = context.getPrincipal();
        if (context.getTokenType().getValue().equals("access_token") && authentication instanceof UsernamePasswordAuthenticationToken) {
            final var principal = authentication.getPrincipal();
            Assert.isInstanceOf(AuthorizationUserDetails.class, principal);
            AuthorizationUserDetails userDetails = (AuthorizationUserDetails) principal;
            var authority = userDetails.getAuthorities()
                                       .stream()
                                       .map(GrantedAuthority::getAuthority)
                                       .collect(Collectors.toSet());
            context.getClaims().claims(map -> {
                map.put(CustomizeOAuth2ParameterNames.UUID, UUIDUtil.getUuid());
                map.put(CustomizeOAuth2ParameterNames.USER_ID, userDetails.getId());
                map.put(CustomizeOAuth2ParameterNames.AUTHORITIES, authority);
                Object scope = map.get(OAuth2IntrospectionClaimNames.SCOPE);
                if (scope instanceof Collection) {
                    Collection<String> collection = this.castAuthoritiesToCollection(scope);
                    collection.addAll(Collections.emptySet());
                }
            });
            Context.setShareObject(AuthorizationUserDetails.class, userDetails);
            log.debug("login username: {}", userDetails.getUsername());
            log.debug("token authorities: {}", authority);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<String> castAuthoritiesToCollection(Object authorities) {
        return (Collection<String>) authorities;
    }
}
