/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zf1976.mayi.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.zf1976.mayi.auth.filter.handler.access.Oauth2AccessDeniedHandler;
import com.zf1976.mayi.auth.filter.handler.access.Oauth2AuthenticationEntryPoint;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * OAuth Authorization Server Configuration.
 *
 * @author Steve Riesenberg
 */
@Configuration
public class OAuth2AuthorizationServerSecurityConfiguration {

	private final SecurityProperties properties;
	private final UserDetailsService userDetailsService;

	public OAuth2AuthorizationServerSecurityConfiguration(SecurityProperties properties, UserDetailsService userDetailsService) {
		this.properties = properties;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * authentication server key pair
	 *
	 * @return {@link KeyPair}
	 */
	@Bean
	@DependsOn(value = "securityProperties")
	@ConditionalOnMissingBean
	public KeyPair keyPair() {
		ClassPathResource classPathResource = new ClassPathResource("root.jks");
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource, properties.getRsaSecret().toCharArray());
		return keyStoreKeyFactory.getKeyPair("root", properties.getRsaSecret().toCharArray());
	}

	/**
	 * 授权处理
	 *
	 * @param http http security
	 * @return {@link SecurityFilterChain}
	 * @throws Exception e
	 */
	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		return http.formLogin(Customizer.withDefaults()).build();
	}

	/**
	 * 标准安全处理
	 *
	 * @param http http security
	 * @return {@link SecurityFilterChain}
	 * @throws Exception e
	 */
	@Bean
	@Order(2)
	public SecurityFilterChain standardSecurityFilterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.cors()
			.and()
			.csrf().disable()
			.formLogin()
			.and()
			.headers().frameOptions().disable()
			.and()
			.exceptionHandling().accessDeniedHandler(new Oauth2AccessDeniedHandler())
			.authenticationEntryPoint(new Oauth2AuthenticationEntryPoint())
			.and()
			.userDetailsService(this.userDetailsService)
			// 授权认证处理
			.authorizeHttpRequests((authorize) -> authorize
							.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
							// 兼容旧版本接口
							.antMatchers("/oauth2/token_key").permitAll()
							.antMatchers(properties.getIgnoreUri()).permitAll()
							.anyRequest()
							.authenticated()
								  )
			.formLogin(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		// @formatter:off
		RegisteredClient loginClient = RegisteredClient.withId(UUID.randomUUID().toString())
													   .clientId("login-client")
													   .clientSecret("{noop}openid-connect")
													   .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
													   .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
													   .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
													   .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
													   .redirectUri("http://127.0.0.1:9000/login/oauth2/code/login-client")
													   .redirectUri("http://127.0.0.1:9000/authorized")
				.scope(OidcScopes.OPENID)
				.scope(OidcScopes.PROFILE)
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
				.build();
		RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("messaging-client")
				.clientSecret("{noop}secret")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.scope("message:read")
				.scope("message:write")
				.build();
		// @formatter:on

		return new InMemoryRegisteredClientRepository(loginClient, registeredClient);
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource(KeyPair keyPair) {
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// @formatter:off
		RSAKey rsaKey = new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.build();
		// @formatter:on
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	/**
	 * token 解码器
	 * @param keyPair 密钥对
	 * @return {@link JwtDecoder}
	 */
	@Bean
	public JwtDecoder jwtDecoder(KeyPair keyPair) {
		return NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic()).build();
	}

	/**
	 * 授权端点配置配置
	 *
	 * @return {@link ProviderSettings}
	 */
	@SuppressWarnings("SpellCheckingInspection")
	@Bean
	public ProviderSettings providerSettings() {
		return ProviderSettings.builder()
							   .authorizationEndpoint("/oauth2/authorize")
							   .tokenEndpoint("/oauth2/token")
							   .jwkSetEndpoint("/oauth2/jwks")
							   .tokenRevocationEndpoint("/oauth2/revoke")
							   .tokenIntrospectionEndpoint("/oauth2/introspect")
							   .oidcClientRegistrationEndpoint("/connect/register")
							   .issuer(this.properties.getTokenIssuer())
							   .build();
	}

}
