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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

/**
 * OAuth Authorization Server Configuration.
 * <p>
 * {@link org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2ClientAuthenticationConfigurer} -> OAuth2ClientAuthenticationFilter
 * {@link org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2TokenEndpointConfigurer} -> OAuth2TokenEndpointFilter
 * {@link OAuth2AuthorizationServerConfiguration}
 * {@link org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationEndpointConfigurer} -> OAuth2AuthorizationEndpointFilter
 *
 * @author Steve Riesenberg
 */
@SuppressWarnings("DanglingJavadoc")
@Configuration
public class OAuth2AuthorizationServerSecurityConfiguration {

	private final SecurityProperties properties;
	private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";
	private final JdbcOperations jdbcOperations;
	private final PasswordEncoder passwordEncoder;
	private volatile RegisteredClientRepository registeredClientRepository;

	public OAuth2AuthorizationServerSecurityConfiguration(SecurityProperties properties, JdbcOperations jdbcOperations, PasswordEncoder passwordEncoder) {
		this.properties = properties;
		this.jdbcOperations = jdbcOperations;
		this.passwordEncoder = passwordEncoder;
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
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer =
				new OAuth2AuthorizationServerConfigurer<>();

		// authorization endpoint
		authorizationServerConfigurer
				.authorizationEndpoint(authorizationEndpoint ->
						authorizationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI));

		// /oauth2/token
		authorizationServerConfigurer.tokenEndpoint(oAuth2TokenEndpointConfigurer -> {
			final var delegatingAuthenticationConverter = new DelegatingAuthenticationConverter(Arrays.asList(
					new OAuth2AuthorizationCodeAuthenticationConverter(),
					new OAuth2ClientCredentialsAuthenticationConverter(),
					new OAuth2RefreshTokenAuthenticationConverter(),
					new BasicAuthenticationConverter()));
			oAuth2TokenEndpointConfigurer.accessTokenRequestConverter(delegatingAuthenticationConverter);
		});
		RequestMatcher endpointsMatcher = authorizationServerConfigurer
				.getEndpointsMatcher();


		http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(new Oauth2AccessDeniedHandler())
																												 .authenticationEntryPoint(new Oauth2AuthenticationEntryPoint()))
			.requestMatcher(endpointsMatcher)
			.authorizeRequests(authorizeRequests ->
							authorizeRequests.anyRequest().authenticated()
							  )
			.csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
			.apply(authorizationServerConfigurer);
		return http.formLogin(Customizer.withDefaults()).build();
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository() {

		if (this.registeredClientRepository == null) {
			synchronized (this) {
				if (this.registeredClientRepository == null) {
					this.registeredClientRepository = new JdbcRegisteredClientRepository(this.jdbcOperations);
					final var registeredClientParametersMapper = new JdbcRegisteredClientRepository.RegisteredClientParametersMapper();
					registeredClientParametersMapper
							.setPasswordEncoder(this.passwordEncoder);
					// init
					final var jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(this.jdbcOperations);
					jdbcRegisteredClientRepository.setRegisteredClientParametersMapper(registeredClientParametersMapper);
				}
			}
		}

		return this.registeredClientRepository;
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
	 * filter list
	 * {@link org.springframework.security.oauth2.server.authorization.web.OAuth2TokenRevocationEndpointFilter} -> /oauth2/revoke 令牌撤销点
	 * {@link org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter} -> /oauth2/token 令牌端点
	 * {@link org.springframework.security.oauth2.server.authorization.web.OAuth2ClientAuthenticationFilter} -> 客户端认证
	 * {@link org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter} -> /oauth2/authorize 授权端点
	 * {@link org.springframework.security.oauth2.server.authorization.web.OAuth2TokenIntrospectionEndpointFilter} -> /oatuh2/introspect 令牌自省端点, 说白了就是返回令牌信息
	 *
	 * authentication provider list
	 * {@link org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationProvider}
	 * {@link org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationProvider}
	 * {@link org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationProvider}
	 * {@link org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationProvider}
	 * {@link org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenIntrospectionAuthenticationProvider}
	 * {@link org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenRevocationAuthenticationProvider}
	 * {@link org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider}
	 * {@link org.springframework.security.config.oauth2.client.CommonOAuth2Provider} -> 支持GOOGLE，GITHUB，FACEBOOK,OKTA
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

	@Bean
	public OAuth2AuthorizationConsentService authorizationConsentService(JdbcOperations jdbcOperations) {
		// Will be used by the ConsentController
		return new JdbcOAuth2AuthorizationConsentService(jdbcOperations, this.registeredClientRepository);
	}

	/**
	 * 1. /oauth2/token 认证流程
	 * client_credentials
	 * {@link org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationProvider} ->
	 * {@link org.springframework.security.authentication.ProviderManager} inner -> {@link org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationProvider}
	 *
	 * password
	 * 没有默认 {@link org.springframework.security.web.authentication.AuthenticationConverter}
	 * {@link org.springframework.security.oauth2.server.authorization.web.OAuth2ClientAuthenticationFilter} -> {@link org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter}
	 *
	 *
	 *
	 */

	/**
	 *  程序Process入口过滤 {@link org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter}
	 *  新版本的OAuth2授权认证，认证 Request - Authentication 信息由{@link org.springframework.security.web.authentication.AuthenticationConverter} 处理.
	 *  统一由{@link org.springframework.security.authentication.ProviderManager} 认证处理，实际逻辑由内部多个{@link org.springframework.security.authentication.AuthenticationProvider} 中一个处理。
	 *
	 */
}
