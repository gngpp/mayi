package com.zf1976.mayi.auth.config;

import com.zf1976.mayi.auth.SecurityContextHolder;
import com.zf1976.mayi.auth.enhance.JdbcClientDetailsServiceEnhancer;
import com.zf1976.mayi.auth.enhance.JwtTokenEnhancer;
import com.zf1976.mayi.auth.enhance.RedisTokenStoreEnhancer;
import com.zf1976.mayi.auth.filter.provider.DaoAuthenticationEnhancerProvider;
import com.zf1976.mayi.auth.grant.RefreshTokenEnhancerGranter;
import com.zf1976.mayi.auth.grant.ResourceOwnerPasswordTokenEnhancerGranter;
import com.zf1976.mayi.auth.filter.handler.access.Oauth2AccessDeniedHandler;
import com.zf1976.mayi.auth.filter.handler.access.Oauth2AuthenticationEntryPoint;
import com.zf1976.mayi.auth.interceptor.EndpointReturnInterceptor;
import com.zf1976.mayi.common.component.validate.service.CaptchaService;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.Assert;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mac
 * @date 2021/2/10
 **/
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter implements SmartLifecycle {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JdbcClientDetailsServiceEnhancer jdbcClientDetailsServiceEnhancer;
    private final CaptchaService captchaService;
    private final SecurityProperties securityProperties;
    private AuthorizationServerSecurityConfigurer authorizationServerSecurityConfigurer;
    private final RedisTemplate<Object, Object> template;
    private final KeyPair keyPair;
    private boolean isRunning = false;

    public OAuth2AuthorizationServerConfiguration(UserDetailsService userDetailsService,
                                                  PasswordEncoder passwordEncoder,
                                                  AuthenticationManager authenticationManager,
                                                  SecurityProperties securityProperties,
                                                  RedisTemplate<Object, Object> template,
                                                  JdbcClientDetailsServiceEnhancer jdbcClientDetailsServiceEnhancer,
                                                  CaptchaService captchaService,
                                                  KeyPair keyPair) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.securityProperties = securityProperties;
        this.template = template;
        this.jdbcClientDetailsServiceEnhancer = jdbcClientDetailsServiceEnhancer;
        this.captchaService = captchaService;
        this.keyPair = keyPair;
        SecurityContextHolder.setShareObject(JdbcClientDetailsServiceEnhancer.class, this.jdbcClientDetailsServiceEnhancer);
    }

    /**
     * 允许表单认证
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        this.authorizationServerSecurityConfigurer = security;
        Assert.notNull(this.authorizationServerSecurityConfigurer,"authorizationServerSecurityConfigurer object cannot been null");
        security.allowFormAuthenticationForClients()
                .authenticationEntryPoint(new Oauth2AuthenticationEntryPoint())
                .accessDeniedHandler(new Oauth2AccessDeniedHandler())
                // oauth/check_token 允许访问
                .checkTokenAccess("permitAll()")
                // oauth/token_key 公开密钥
                .tokenKeyAccess("permitAll()")
                .passwordEncoder(passwordEncoder)
                .addTokenEndpointAuthenticationFilter((request,response, chain) -> {
                    chain.doFilter(request, response);
                });
    }

    /**
     * 客户端服务配置
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        this.jdbcClientDetailsServiceEnhancer.setPasswordEncoder(passwordEncoder);
        clients.withClientDetails(this.jdbcClientDetailsServiceEnhancer);
    }

    /**
     * 认证端点
     * refresh_token有两种使用方式：重复使用(true)、非重复使用(false)，默认为true
     * 1.重复使用：access_token过期刷新时， refresh token过期时间未改变，仍以初次生成的时间为准
     * 2.非重复使用：access_token过期刷新时， refresh_token过期时间延续，在refresh_token有效期内刷新而
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        RedisConnectionFactory redisConnectionFactory = this.template.getRequiredConnectionFactory();
        var tokenStore = new RedisTokenStoreEnhancer(redisConnectionFactory, securityProperties);
        SecurityContextHolder.setShareObject(TokenStore.class, tokenStore);
        endpoints.authenticationManager(authenticationManager)
                 .tokenStore(tokenStore)
                 .tokenEnhancer(tokenEnhancerChain())
                 .accessTokenConverter(jwtAccessTokenConverter())
                 .userDetailsService(userDetailsService)
                 .reuseRefreshTokens(false)
                 .exceptionTranslator(new DefaultWebResponseExceptionTranslator())
                 .addInterceptor(new EndpointReturnInterceptor());
        // 自定义授权
        endpoints.tokenGranter(this.generateTokenGranter(endpoints));
    }

    private TokenGranter generateTokenGranter(AuthorizationServerEndpointsConfigurer endpointsConfigurer) {
        var tokenGranters = getTokenGranters(endpointsConfigurer);
        return new CompositeTokenGranter(tokenGranters);
    }

    /**
     * 获取 granter
     *
     * @date 2021-03-25 12:10:57
     * @param endpoints 端点
     * @return {@link List<TokenGranter>}
     */
    private List<TokenGranter> getTokenGranters(AuthorizationServerEndpointsConfigurer endpoints) {
        ClientDetailsService clientDetails = endpoints.getClientDetailsService();
        AuthorizationServerTokenServices tokenServices = endpoints.getTokenServices();
        AuthorizationCodeServices authorizationCodeServices = endpoints.getAuthorizationCodeServices();
        OAuth2RequestFactory requestFactory = endpoints.getOAuth2RequestFactory();
        List<TokenGranter> tokenGranters = new ArrayList<>();
        tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetails, requestFactory));
        tokenGranters.add(new RefreshTokenEnhancerGranter(tokenServices, clientDetails, requestFactory));
        tokenGranters.add(new ImplicitTokenGranter(tokenServices, clientDetails, requestFactory));
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetails, requestFactory));
        if (this.authenticationManager != null) {
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(this.authenticationManager, tokenServices, clientDetails, requestFactory));
            tokenGranters.add(new ResourceOwnerPasswordTokenEnhancerGranter(this.authenticationManager, tokenServices, clientDetails, requestFactory, captchaService));
        }
        return tokenGranters;
    }

    private JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(this.keyPair);
        return jwtAccessTokenConverter;
    }

    private TokenEnhancerChain tokenEnhancerChain() {
        var enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> enhancerList = Arrays.asList(new JwtTokenEnhancer(), jwtAccessTokenConverter());
        enhancerChain.setTokenEnhancers(enhancerList);
        return enhancerChain;
    }

    /**
     * 所有bean初始化完成
     * 增强OAuth2 Provide客户端处理
     */
    @Override
    public void start() {
        Assert.notNull(this.authorizationServerSecurityConfigurer, "authorizationServerSecurityConfigurer cannot been null!");
        ClientDetailsUserDetailsService clientDetailsUserDetailsService = new ClientDetailsUserDetailsService(this.jdbcClientDetailsServiceEnhancer);
        DaoAuthenticationEnhancerProvider daoClientAuthenticationEnhancerProvider = new DaoAuthenticationEnhancerProvider(this.passwordEncoder, clientDetailsUserDetailsService);
        this.authorizationServerSecurityConfigurer.and().authenticationProvider(daoClientAuthenticationEnhancerProvider);
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.isRunning = false;
    }


    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

}
