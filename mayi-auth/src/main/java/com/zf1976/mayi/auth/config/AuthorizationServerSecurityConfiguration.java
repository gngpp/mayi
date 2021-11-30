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

package com.zf1976.mayi.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.zf1976.mayi.auth.oauth2.authorization.CustomizeOAuthorizationRowMapper;
import com.zf1976.mayi.auth.oauth2.authorization.OAuth2JwtTokenCustomizer;
import com.zf1976.mayi.auth.oauth2.convert.OAuth2ResourceOwnerPasswordAuthenticationConverter;
import com.zf1976.mayi.auth.oauth2.provider.CustomizeDaoAuthenticationProvider;
import com.zf1976.mayi.auth.oauth2.provider.OAuth2ResourceOwnerPasswordAuthenticationProvider;
import com.zf1976.mayi.auth.oauth2.repository.CustomizeJdbcRegisteredClientRepository;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
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
public class AuthorizationServerSecurityConfiguration {

	private final Logger log = LoggerFactory.getLogger("[AuthorizationServerSecurityConfiguration]");
	private final SecurityProperties properties;
	private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";
	private final JdbcOperations jdbcOperations;
	private final PasswordEncoder passwordEncoder;
	private final UserDetailsService userDetailsService;
	private volatile RegisteredClientRepository registeredClientRepository;
	private volatile OAuth2TokenCustomizer<JwtEncodingContext> jwtEncodingContextOAuth2TokenCustomizer;

	public AuthorizationServerSecurityConfiguration(SecurityProperties properties,
													JdbcOperations jdbcOperations,
													UserDetailsService userDetailsService,
													PasswordEncoder passwordEncoder) {
		this.properties = properties;
		this.jdbcOperations = jdbcOperations;
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
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
				.authorizationEndpoint(configurer ->
						configurer.consentPage(CUSTOM_CONSENT_PAGE_URI));

		// /oauth2/token
		authorizationServerConfigurer.tokenEndpoint(configurer -> {
			final var delegatingAuthenticationConverter = new DelegatingAuthenticationConverter(Arrays.asList(
					new OAuth2AuthorizationCodeAuthenticationConverter(),
					new OAuth2ClientCredentialsAuthenticationConverter(),
					new OAuth2RefreshTokenAuthenticationConverter(),
					new OAuth2ResourceOwnerPasswordAuthenticationConverter())
			);
			configurer.accessTokenRequestConverter(delegatingAuthenticationConverter);
		});
		RequestMatcher endpointsMatcher = authorizationServerConfigurer
				.getEndpointsMatcher();

		// Allow security to be processed only at the oauth2 authentication endpoint
		http.requestMatcher(endpointsMatcher)
			.authorizeRequests(authorizeRequests ->
							authorizeRequests.anyRequest().authenticated()
							  )
			// ignored oauth2 endpoint url
			.csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
			.apply(authorizationServerConfigurer);
		final var filterChain = http.formLogin(Customizer.withDefaults()).build();
		// initialization is complete add password grant type
		this.addCustomOAuth2ResourceOwnerPasswordAuthenticationProvider(http);
		return filterChain;
	}

	private void addCustomOAuth2ResourceOwnerPasswordAuthenticationProvider(HttpSecurity http) {

		AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
		ProviderSettings providerSettings = http.getSharedObject(ProviderSettings.class);
		OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);
		JwtEncoder jwtEncoder = http.getSharedObject(JwtEncoder.class);
		OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer = this.jwtCustomizer();
		OAuth2ResourceOwnerPasswordAuthenticationProvider resourceOwnerPasswordAuthenticationProvider =
				new OAuth2ResourceOwnerPasswordAuthenticationProvider(authenticationManager, authorizationService, jwtEncoder);
		if (jwtCustomizer != null) {
			resourceOwnerPasswordAuthenticationProvider.setJwtCustomizer(jwtCustomizer);
		}

		resourceOwnerPasswordAuthenticationProvider.setProviderSettings(providerSettings);

		// This will add new authentication provider in the list of existing authentication providers.
		http.authenticationProvider(new CustomizeDaoAuthenticationProvider(this.passwordEncoder, this.userDetailsService));
		http.authenticationProvider(resourceOwnerPasswordAuthenticationProvider);

	}

	@Bean
	public RegisteredClientRepository registeredClientRepository() {

		if (this.registeredClientRepository == null) {
			synchronized (this) {
				if (this.registeredClientRepository == null) {
					final var registeredClientParametersMapper = new JdbcRegisteredClientRepository.RegisteredClientParametersMapper();
					registeredClientParametersMapper
							.setPasswordEncoder(this.passwordEncoder);
					// init
					final var jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(this.jdbcOperations);
					jdbcRegisteredClientRepository.setRegisteredClientParametersMapper(registeredClientParametersMapper);
					this.registeredClientRepository = new CustomizeJdbcRegisteredClientRepository(jdbcOperations, jdbcRegisteredClientRepository);
				}
			}
		}

		return this.registeredClientRepository;
	}

	/**
	 * grant_type authorization_code consent authorization
	 *
	 * @param jdbcOperations jdbc
	 * @return {@link OAuth2AuthorizationConsentService}
	 */
	@Bean
	@DependsOn({"jdbcTemplate", "registeredClientRepository"})
	public OAuth2AuthorizationConsentService authorizationConsentService(JdbcOperations jdbcOperations, RegisteredClientRepository registeredClientRepository) {
		// Will be used by the ConsentController
		return new JdbcOAuth2AuthorizationConsentService(jdbcOperations, registeredClientRepository);
	}

	/**
	 * order grant_type authorization
	 *
	 * @param jdbcOperations jdbc
	 * @return {@link OAuth2AuthorizationService}
	 */
	@Bean
	@DependsOn({"jdbcTemplate", "registeredClientRepository"})
	public OAuth2AuthorizationService auth2AuthorizationService(JdbcOperations jdbcOperations, RegisteredClientRepository registeredClientRepository) {
		final var jdbcOAuth2AuthorizationService = new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository);
		jdbcOAuth2AuthorizationService.setAuthorizationRowMapper(new CustomizeOAuthorizationRowMapper(registeredClientRepository));
		return jdbcOAuth2AuthorizationService;
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource(KeyPair keyPair) {
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAKey rsaKey = new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.build();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	/**
	 * token 解码器
	 *
	 * @param keyPair 密钥对
	 * @return {@link JwtDecoder}
	 */
	@Bean
	public JwtDecoder jwtDecoder(KeyPair keyPair) {
		return NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic()).build();
	}


	/**
	 * Personalise JWT token
	 */
	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
		if (this.jwtEncodingContextOAuth2TokenCustomizer == null) {
			synchronized (this) {
				if (this.jwtEncodingContextOAuth2TokenCustomizer == null) {
					this.jwtEncodingContextOAuth2TokenCustomizer = new OAuth2JwtTokenCustomizer();
				}
			}
		}
		return this.jwtEncodingContextOAuth2TokenCustomizer;
	}

	/**
	 * 授权端点配置配置
	 *
	 * filter list
	 * {@link org.springframework.security.oauth2.server.authorization.web.OAuth2TokenRevocationEndpointFilter} -> /oauth2/revoke 令牌撤销点
	 * {@link org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter} -> /oauth2/token 令牌端点
	 * {@link org.springframework.security.oauth2.server.authorization.web.OAuth2ClientAuthenticationFilter} -> 客户端认证
	 * {@link org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter} -> /oauth2/authorize 授权端点
	 * {@link org.springframework.security.oauth2.server.authorization.web.OAuth2TokenIntrospectionEndpointFilter} -> /oauth2/introspect 令牌自省端点, 说白了就是返回令牌信息
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

	/**
	 * 坑
	 * 1. /oauth2/introspect 端点令牌信息展示不全
	 * 2. OAuth2ConfigurerUtils, 小玩意，共享对象
	 */


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
