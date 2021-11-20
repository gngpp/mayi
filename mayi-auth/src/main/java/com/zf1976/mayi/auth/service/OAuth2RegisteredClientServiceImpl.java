package com.zf1976.mayi.auth.service;

import com.zf1976.mayi.auth.exception.ClientException;
import com.zf1976.mayi.auth.oauth2.repository.CustomizeJdbcRegisteredClientRepository;
import com.zf1976.mayi.auth.oauth2.repository.CustomizeRegisteredClientRepository;
import com.zf1976.mayi.auth.oauth2.repository.Page;
import com.zf1976.mayi.auth.pojo.*;
import com.zf1976.mayi.auth.pojo.dto.RegisteredClientDTO;
import com.zf1976.mayi.auth.pojo.vo.RegisteredClientVO;
import com.zf1976.mayi.common.core.util.UUIDUtil;
import com.zf1976.mayi.common.core.validate.Validator;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JdbcOperations open transaction by default
 *
 * @author mac
 * 2021/11/12 星期五 5:03 下午
 */
@SuppressWarnings("FieldCanBeLocal")
@Service
@Transactional(readOnly = true, rollbackFor = Throwable.class)
public class OAuth2RegisteredClientServiceImpl implements OAuth2RegisteredClientService{

    /**
     * all time units are in seconds
     */
    private static final Pattern ID_SECRET_PATTERN = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{10,20}$");
    private final int tokenMinTime = 3600;
    private final int tokenRefreshMinTime = 7200;
    private final int tokenMaxTime = 2678400;
    private final int tokenRefreshMaxTime = 5356800;
    private final Set<String> authorizationGrantTypeSet = Stream.of(AuthorizationGrantType.AUTHORIZATION_CODE,
                                                                        AuthorizationGrantType.CLIENT_CREDENTIALS,
                                                                        AuthorizationGrantType.REFRESH_TOKEN,
                                                                        AuthorizationGrantType.PASSWORD)
                                                                .map(AuthorizationGrantType::getValue)
                                                                .collect(Collectors.toSet());

    private final Set<String> clientAuthenticationMethodSet = Stream.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                                                                            ClientAuthenticationMethod.CLIENT_SECRET_JWT,
                                                                            ClientAuthenticationMethod.CLIENT_SECRET_POST)
                                                                    .map(ClientAuthenticationMethod::getValue)
                                                                    .collect(Collectors.toSet());

    private final Set<String> signatureAlg = Arrays.stream(SignatureAlgorithm.values())
                                                   .map(SignatureAlgorithm::getName)
                                                   .collect(Collectors.toSet());

    private final CustomizeRegisteredClientRepository clientRepository;

    public OAuth2RegisteredClientServiceImpl(JdbcOperations jdbcOperations) {
        this.clientRepository = new CustomizeJdbcRegisteredClientRepository(jdbcOperations);
    }

    @Override
    @Transactional
    public Void sava(RegisteredClientDTO registeredClientDTO) {
        // update
        if (registeredClientDTO.getId() != null) {
            this.updateRegisteredClient(registeredClientDTO);
        } else {
            this.insertRegisteredClient(registeredClientDTO);
        }
        return null;
    }

    @Override
    public RegisteredClientVO findById(String id) {
        Assert.notNull(id, "client primaryKey cannot been null.");
        RegisteredClient registeredClient = this.clientRepository.findById(id);
        return this.toVo(registeredClient);
    }

    @Override
    public RegisteredClientVO findByClientId(String clientId) {
        Assert.notNull(clientId, "client id cannot been null.");
        RegisteredClient registeredClient = this.clientRepository.findByClientId(clientId);
        return this.toVo(registeredClient);
    }

    @Override
    public Page<RegisteredClientVO> findList(Page<?> page) {
        Assert.notNull(page, "query page parameter cannot be empty.");
        Page<RegisteredClient> registeredClientPage = this.clientRepository.findClientList(page);
        return this.mapPageToTarget(registeredClientPage, this::toVo);
    }

    @Override
    @Transactional
    public Void removeById(String id) {
        Assert.notNull(id, "client primaryKey cannot been null.");
        this.clientRepository.removeById(id);
        return null;
    }

    @Override
    @Transactional
    public Void removeByClientId(String clientId) {
        Assert.notNull(clientId, "client id cannot been null.");
        this.clientRepository.removeByClientId(clientId);
        return null;
    }

    @Override
    @Transactional
    public Void removeByIdList(Set<String> idList) {
        this.clientRepository.removeByIdList(new ArrayList<>(idList));
        return null;
    }

    @Override
    @Transactional
    public Void removeByClientIdList(Set<String> clientIdList) {
        this.clientRepository.removeByClientIdList(new ArrayList<>(clientIdList));
        return null;
    }

    protected void insertRegisteredClient(RegisteredClientDTO registeredClientDTO) {
        if (this.clientRepository.findByClientId(registeredClientDTO.getClientId()) != null) {
            throw new ClientException("client id: " + registeredClientDTO.getClientId() + " exist.");
        }
        this.validateForm(registeredClientDTO);
        RegisteredClient registeredClient = this.toEntity(registeredClientDTO);
        this.clientRepository.save(registeredClient);
    }

    protected void updateRegisteredClient(RegisteredClientDTO registeredClientDTO) {
        RegisteredClient registeredClient = this.clientRepository.findById(registeredClientDTO.getId());
        if (registeredClient == null) {
            throw new ClientException("client does not exist.");
        }
        this.validateForm(registeredClientDTO);
        // validate client id
        if (!ObjectUtils.nullSafeEquals(registeredClient.getClientId(), registeredClientDTO.getClientId())) {
            if (this.clientRepository.findByClientId(registeredClientDTO.getClientId()) != null) {
                throw new ClientException("client id: " + registeredClientDTO.getClientId() + " exist.");
            }
        }

        RegisteredClient entity = this.toEntity(registeredClientDTO);
        this.clientRepository.save(entity);
    }

    /**
     * 分页对象拷贝
     *
     * @param sourcePage 原对象
     * @param translator func
     * @return 转换结果
     */
    protected Page<RegisteredClientVO> mapPageToTarget(Page<RegisteredClient> sourcePage,
                                                       Function<RegisteredClient, RegisteredClientVO> translator) {
        final List<RegisteredClientVO> target = sourcePage.getRecords()
                                                          .stream()
                                                          .map(translator)
                                                          .collect(Collectors.toList());

        return Page.<RegisteredClientVO>from(sourcePage)
                   .setRecords(target);
    }

        /**
         * 校验表单数据
         *
         * @param dto DTO
         */
    private void validateForm(RegisteredClientDTO dto) {
        // 校验客户端ID，Secret是否合格
        Validator.of(dto)
                 // 校验客户端ID是否合格
                 .withValidated(data -> this.validateIdAndSecret(data.getClientId()),
                         () -> new ClientException("Client ID does not meet the requirements"))
                 // 校验客户端密钥是否合格
                 .withValidated(data -> this.validateIdAndSecret(data.getClientSecret()),
                         () -> new ClientException("Secret does not meet the requirements"))
                 // 校验token有效时间范围
                 .withValidated(data -> this.validateTokenTimeScope(data.getTokenSettings().getAccessTokenTimeToLive()),
                         () -> new ClientException("The token validity time does not meet the requirements"))
                 // 校验refresh token有效时间范围
                 .withValidated(data -> this.validateRefreshTokenTimeScope(data.getTokenSettings().getRefreshTokenTimeToLive()),
                         () -> new ClientException("The refresh token validity time does not meet the requirements"))
                 // 校验认证模式
                 .withValidated(data -> this.validateGrantType(data.getAuthorizationGrantTypes()),
                         () -> new ClientException("The certification model does not meet the requirements"))
                 // 校验签名算法
                .withValidated(data -> this.validateSignAlg(data.getTokenSettings().getIdTokenSignatureAlgorithm()),
                        () -> new ClientException("The signature algorithm does not meet the requirements"));
    }

    /**
     * 正则校验客户端ID、Secret
     *
     * @param value string
     * @return {@link boolean}
     */
    private boolean validateIdAndSecret(String value) {
        return ID_SECRET_PATTERN.matcher(value)
                                .find();
    }

    /**
     * 校验签名算法
     *
     * @param name alg name
     * @return {@link boolean}
     */
    private boolean validateSignAlg(String name) {
        return this.signatureAlg.contains(name);
    }

    /**
     * 校验refresh token有效时间范围
     *
     * @param value 值
     * @return {@link boolean}
     */
    private boolean validateRefreshTokenTimeScope(Long value) {
        if (value != null) {
            return value >= this.tokenRefreshMinTime && value <= this.tokenRefreshMaxTime;
        }
        return false;
    }

    /**
     * 校验token有效时间范围
     *
     * @param value 值
     * @return {@link boolean}
     */
    private boolean validateTokenTimeScope(Long value) {
        if (value != null) {
            return value >= this.tokenMinTime && value <= this.tokenMaxTime;
        }
        return false;
    }

    /**
     * 校验授权类型
     *
     * @param grantTypesConverts 授权类型转换集合
     */
    private boolean validateGrantType(Set<AuthorizationGrantTypesConvert> grantTypesConverts) {
        return grantTypesConverts.stream()
                                 .map(AuthorizationGrantTypesConvert::getValue)
                                 .allMatch(this.authorizationGrantTypeSet::contains);
    }

    /**
     * 校验客户端认证方法
     *
     * @param authenticationMethodsConverts 客户端认证方法转换集合
     */
    private boolean validateClientAuthenticationMethod(Set<ClientAuthenticationMethodsConvert> authenticationMethodsConverts) {

        return authenticationMethodsConverts.stream()
                                            .map(ClientAuthenticationMethodsConvert::getValue)
                                            .allMatch(this.clientAuthenticationMethodSet::contains);
    }

    private RegisteredClient toEntity(RegisteredClientDTO registeredClientDTO) {

        if (registeredClientDTO == null) {
            return null;
        }

        Set<AuthorizationGrantType> authorizationGrantTypes = registeredClientDTO.getAuthorizationGrantTypes()
                                                                .stream()
                                                                .map(AuthorizationGrantTypesConvert::getValue)
                                                                .map(AuthorizationGrantType::new)
                                                                .collect(Collectors.toSet());
        Set<ClientAuthenticationMethod> clientAuthenticationMethods = registeredClientDTO.getClientAuthenticationMethods()
                                                                    .stream()
                                                                    .map(ClientAuthenticationMethodsConvert::getValue)
                                                                    .map(ClientAuthenticationMethod::new)
                                                                    .collect(Collectors.toSet());
        ClientSettingsConvert clientSettingsConvert = registeredClientDTO.getClientSettings();
        ClientSettings clientSettings = ClientSettings.builder()
                                             .requireProofKey(clientSettingsConvert.getRequireProofKey())
                                             .requireAuthorizationConsent(clientSettingsConvert.getRequireAuthorizationConsent())
                                             .build();

        TokenSettingsConvert tokenSettingsConvert = registeredClientDTO.getTokenSettings();
        TokenSettings tokenSettings = TokenSettings.builder()
                                           .accessTokenTimeToLive(Duration.ofSeconds(tokenSettingsConvert.getAccessTokenTimeToLive()))
                                           .refreshTokenTimeToLive(Duration.ofSeconds(tokenSettingsConvert.getRefreshTokenTimeToLive()))
                                           .idTokenSignatureAlgorithm(SignatureAlgorithm.from(tokenSettingsConvert.getIdTokenSignatureAlgorithm()))
                                           .reuseRefreshTokens(tokenSettingsConvert.isReuseRefreshTokens())
                                           .build();
        String id = registeredClientDTO.getId() != null ? registeredClientDTO.getId() : UUIDUtil.getUuid();
        return RegisteredClient.withId(id)
                               .clientId(registeredClientDTO.getClientId())
                               .clientName(registeredClientDTO.getClientName())
                               .clientSecret(registeredClientDTO.getClientSecret())
                               .clientIdIssuedAt(Instant.now().atZone(ZoneId.systemDefault()).toInstant())
                               .clientSecretExpiresAt(Instant.ofEpochMilli(registeredClientDTO.getClientSecretExpiresAt()))
                               .clientAuthenticationMethods(v -> v.addAll(clientAuthenticationMethods))
                               .authorizationGrantTypes(v -> v.addAll(authorizationGrantTypes))
                               .clientSettings(clientSettings)
                               .tokenSettings(tokenSettings)
                               .build();
    }


    private RegisteredClientVO toVo(RegisteredClient registeredClient) {

        if (registeredClient == null) {
            return new RegisteredClientVO();
        }

        Set<AuthorizationGrantTypesConvert> grantTypesConvertList = registeredClient.getAuthorizationGrantTypes()
                                                                                    .stream()
                                                                                    .map(AuthorizationGrantType::getValue)
                                                                                    .map(AuthorizationGrantTypesConvert::new)
                                                                                    .collect(Collectors.toSet());

        Set<ClientAuthenticationMethodsConvert> authenticationMethodsConverts = registeredClient.getClientAuthenticationMethods()
                                                                                                .stream()
                                                                                                .map(ClientAuthenticationMethod::getValue)
                                                                                                .map(ClientAuthenticationMethodsConvert::new)
                                                                                                .collect(Collectors.toSet());

        ClientSettings clientSettings = registeredClient.getClientSettings();
        ClientSettingsConvert clientSettingsConvert = new ClientSettingsConvert(clientSettings.isRequireProofKey(), clientSettings.isRequireAuthorizationConsent());

        TokenSettings tokenSettings = registeredClient.getTokenSettings();
        TokenSettingsConvert tokenSettingsConvert = new TokenSettingsConvert()
                .setIdTokenSignatureAlgorithm(tokenSettings.getIdTokenSignatureAlgorithm().getName())
                .setAccessTokenTimeToLive(tokenSettings.getAccessTokenTimeToLive().toSeconds())
                .setRefreshTokenTimeToLive(tokenSettings.getRefreshTokenTimeToLive().toSeconds())
                .setReuseRefreshTokens(tokenSettings.isReuseRefreshTokens());

        return new RegisteredClientVO()
                .setClientId(registeredClient.getClientId())
                .setId(registeredClient.getId())
                .setClientSecret(registeredClient.getClientSecret())
                .setClientName(registeredClient.getClientName())
                .setAuthorizationGrantTypes(grantTypesConvertList)
                .setScopes(registeredClient.getScopes())
                .setClientAuthenticationMethods(authenticationMethodsConverts)
                .setRedirectUris(registeredClient.getRedirectUris())
                .setScopes(registeredClient.getScopes())
                .setClientIdIssuedAt(registeredClient.getClientIdIssuedAt() == null? null : registeredClient.getClientIdIssuedAt().toEpochMilli())
                .setClientSecretExpiresAt(registeredClient.getClientSecretExpiresAt() == null? null : registeredClient.getClientSecretExpiresAt().toEpochMilli())
                .setClientSettings(clientSettingsConvert)
                .setTokenSettings(tokenSettingsConvert);
    }

}
