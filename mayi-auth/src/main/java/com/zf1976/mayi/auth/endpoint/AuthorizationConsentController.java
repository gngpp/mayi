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
package com.zf1976.mayi.auth.endpoint;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;


@Controller
public class AuthorizationConsentController {
	private final RegisteredClientRepository registeredClientRepository;
	private final OAuth2AuthorizationConsentService authorizationConsentService;

	public AuthorizationConsentController(RegisteredClientRepository registeredClientRepository,
										  OAuth2AuthorizationConsentService authorizationConsentService) {
		this.registeredClientRepository = registeredClientRepository;
		this.authorizationConsentService = authorizationConsentService;
	}

	private static Set<ScopeWithDescription> withDescription(Set<String> scopes) {
		Set<ScopeWithDescription> scopeWithDescriptions = new HashSet<>();
		for (String scope : scopes) {
			scopeWithDescriptions.add(new ScopeWithDescription(scope));

		}
		return scopeWithDescriptions;
	}

	@GetMapping(value = "/oauth2/consent")
	public String consent(Principal principal, Model model,
						  @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
						  @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
						  @RequestParam(OAuth2ParameterNames.STATE) String state) {

		// Remove scopes that were already approved
		Set<String> scopesToApprove = new HashSet<>();
		Set<String> previouslyApprovedScopes = new HashSet<>();
		RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
		Assert.notNull(registeredClient, "registerClient cannot been null!");
		OAuth2AuthorizationConsent currentAuthorizationConsent =
				this.authorizationConsentService.findById(registeredClient.getId(), principal.getName());
		Set<String> authorizedScopes;
		if (currentAuthorizationConsent != null) {
			authorizedScopes = currentAuthorizationConsent.getScopes();
		} else {
			authorizedScopes = Collections.emptySet();
		}
		for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
			if (authorizedScopes.contains(requestedScope)) {
				previouslyApprovedScopes.add(requestedScope);
			} else {
				scopesToApprove.add(requestedScope);
			}
		}

		model.addAttribute("clientId", clientId);
		model.addAttribute("state", state);
		model.addAttribute("scopes", withDescription(scopesToApprove));
		model.addAttribute("previouslyApprovedScopes", withDescription(previouslyApprovedScopes));
		model.addAttribute("principalName", principal.getName());

		return "consent";
	}

	public static class ScopeWithDescription {
		private static final String DEFAULT_DESCRIPTION = "UNKNOWN SCOPE - We cannot provide information about this permission, use caution when granting this.";
		private static final Map<String, String> scopeDescriptions = new HashMap<>();

		static {
			scopeDescriptions.put(
					"message.read",
					"This application will be able to read your message."
								 );
			scopeDescriptions.put(
					"message.write",
					"This application will be able to add new messages. It will also be able to edit and delete existing messages."
								 );
			scopeDescriptions.put(
					"other.scope",
					"This is another scope example of a scope description."
								 );
		}

		public final String scope;
		public final String description;

		ScopeWithDescription(String scope) {
			this.scope = scope;
			this.description = scopeDescriptions.getOrDefault(scope, DEFAULT_DESCRIPTION);
		}
	}
}
