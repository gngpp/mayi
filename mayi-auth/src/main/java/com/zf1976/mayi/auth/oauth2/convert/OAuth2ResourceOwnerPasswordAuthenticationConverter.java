/*
 *
 *  * Copyright (c) 2021 zf1976
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

package com.zf1976.mayi.auth.oauth2.convert;

import com.zf1976.mayi.auth.oauth2.OAuth2ResourceOwnerPasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("DuplicatedCode")
public class OAuth2ResourceOwnerPasswordAuthenticationConverter implements AuthenticationConverter {

	@Override
	public Authentication convert(HttpServletRequest request) {

		// grant_type (REQUIRED)
		String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
		if (!AuthorizationGrantType.PASSWORD.getValue().equals(grantType)) {
			return null;
		}

		MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);

		// scope (OPTIONAL)
		String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
		if (StringUtils.hasText(scope) &&
				parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
			OAuth2EndpointUtils.throwError(
					OAuth2ErrorCodes.INVALID_REQUEST,
					OAuth2ParameterNames.SCOPE,
					OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
		}

		Set<String> requestedScopes = null;
		if (StringUtils.hasText(scope)) {
			requestedScopes = new HashSet<>(
					Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
		}

		// username (REQUIRED)
		String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
		if (!StringUtils.hasText(username) || parameters.get(OAuth2ParameterNames.USERNAME).size() != 1) {
			OAuth2EndpointUtils.throwError(
					OAuth2ErrorCodes.INVALID_REQUEST,
					OAuth2ParameterNames.USERNAME,
					OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
		}

		// password (REQUIRED)
		String password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);
		if (!StringUtils.hasText(password) || parameters.get(OAuth2ParameterNames.PASSWORD).size() != 1) {
			OAuth2EndpointUtils.throwError(
					OAuth2ErrorCodes.INVALID_REQUEST,
					OAuth2ParameterNames.PASSWORD,
					OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
		}

		Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
		if (clientPrincipal == null) {
			OAuth2EndpointUtils.throwError(
					OAuth2ErrorCodes.INVALID_REQUEST,
					OAuth2ErrorCodes.INVALID_CLIENT,
					OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
		}

		Map<String, Object> additionalParameters = parameters
				.entrySet()
				.stream()
				.filter(e -> !e.getKey().equals(OAuth2ParameterNames.GRANT_TYPE) &&
						!e.getKey().equals(OAuth2ParameterNames.SCOPE))
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

		return new OAuth2ResourceOwnerPasswordAuthenticationToken(AuthorizationGrantType.PASSWORD, clientPrincipal, requestedScopes, additionalParameters);

	}

}
