package com.zf1976.mayi.upms.biz.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Splitter;
import com.zf1976.mayi.common.component.cache.annotation.CacheConfig;
import com.zf1976.mayi.common.component.cache.annotation.CacheEvict;
import com.zf1976.mayi.common.component.cache.annotation.CachePut;
import com.zf1976.mayi.common.core.constants.AuthGranterTypeConstants;
import com.zf1976.mayi.common.core.constants.Namespace;
import com.zf1976.mayi.common.core.validate.Validator;
import com.zf1976.mayi.common.encrypt.EncryptUtil;
import com.zf1976.mayi.upms.biz.dao.ClientDetailsDao;
import com.zf1976.mayi.upms.biz.pojo.dto.ClientDetailsDTO;
import com.zf1976.mayi.upms.biz.pojo.po.ClientDetails;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.vo.ClientDetailsVO;
import com.zf1976.mayi.upms.biz.security.convert.ClientConvert;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 2021/11/12 开始作废
 *
 * @author mac
 * @date 2021/4/10
 */
@Service
@SuppressWarnings("all")
@CacheConfig(namespace = Namespace.CLIENT)
@Transactional(rollbackFor = Throwable.class)
@Deprecated
public class OAuth2ClientService extends ServiceImpl<ClientDetailsDao, ClientDetails> {

    private static final Pattern ID_SECRET_PATTERN = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{10,20}$");
    private static final List<String> autoApproveScope = Arrays.asList("false", "true", "read", "write");
    private static final String SCOPE = "all";
    private final int tokenMinTime = 3600;
    private final int tokenRefreshMinTime = 7200;
    private final int tokenMaxTime = 2678400;
    private final int tokenRefreshMaxTime = 5356800;
    private final ClientDetailsDao clientDetailsDao;
    private final ClientConvert convert;

    public OAuth2ClientService(ClientDetailsDao clientDetailsDao) {
        this.convert = ClientConvert.INSTANCE;
        this.clientDetailsDao = clientDetailsDao;
    }

    /**
     * 分页查询
     *
     * @param query 查询对象
     * @return {@link IPage< ClientDetailsVO>}
     * @throws
     */
    @CachePut(key = "#query")
    @Transactional(readOnly = true)
    public IPage<ClientDetailsVO> clientDetailsPage(Query<?> query) {
        Page<ClientDetails> sourcePage = super.lambdaQuery()
                                              .page(query.toPage());
        return this.pageMapToTarget(sourcePage, convert::toClientDetailsVO);
    }


    /**
     * 分页对象类型转换
     *
     * @param sourcePage 源分页对象
     * @param translator 翻译
     * @return {@link IPage < PermissionVO >}
     * @date 2021-05-12 09:37:44
     */
    protected <T> IPage<T> pageMapToTarget(IPage<ClientDetails> sourcePage, Function<ClientDetails, T> translator) {
        List<T> targetPageList = sourcePage.getRecords()
                                           .stream()
                                           .map(translator)
                                           .collect(Collectors.toList());
        return new Page<T>(sourcePage.getCurrent(),
                sourcePage.getSize(),
                sourcePage.getTotal(),
                sourcePage.isSearchCount()).setRecords(targetPageList);
    }

    /**
     * 新增客户端
     *
     * @param dto DTO
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void addClient(ClientDetailsDTO dto) {
        // 校验客户端ID是否合格
        Validator.of(dto.getClientId())
                 .withValidated(data -> this.validateIdAndSecret(data), () -> new SecurityException("ID does not meet the requirements"));
        // 校验客户端是否已存在
        super.lambdaQuery()
             .eq(ClientDetails::getClientId, dto.getClientId())
             .oneOpt()
             .ifPresent(clientDetails -> {
                 throw new SecurityException("Client ID already exists");
             });
        // 校验表单
        this.validateForm(dto);
        // DTO转实体
        ClientDetails clientDetails = this.convert.toClientDetailsEntity(dto);
        // 设置加密密钥
        this.setEncodeCLientSecret(clientDetails, dto.getClientSecret());
        // 新增数据
        if (!super.saveOrUpdate(clientDetails)) {
            throw new SecurityException("Failed to insert client data");
        }
        return null;
    }

    /**
     * 编辑更新客户端，需要重置密钥
     *
     * @param dto DTO
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void editClient(ClientDetailsDTO dto) {
        // 查询客户端
        ClientDetails clientDetails = super.lambdaQuery()
                                           .eq(ClientDetails::getClientId, dto.getClientId())
                                           .oneOpt()
                                           .orElseThrow(() -> new SecurityException("Client does not exist"));
        // 加密后密钥
        final String encodeSecret = DigestUtils.md5DigestAsHex(dto.getClientSecret()
                                                                  .getBytes(StandardCharsets.UTF_8));
        // 强制重置密钥
        if (ObjectUtils.nullSafeEquals(clientDetails.getClientSecret(), encodeSecret)) {
            throw new SecurityException("The client key cannot be duplicated");
        }
        // 校验表单
        this.validateForm(dto);
        // 复制属性
        this.convert.copyProperties(dto, clientDetails);
        // 设置加密密钥
        this.setEncodeCLientSecret(clientDetails, dto.getClientSecret());
        // 新增数据
        if (!super.saveOrUpdate(clientDetails)) {
            throw new SecurityException("Failed to insert client data");
        }
        return null;
    }

    /**
     * 设置加密密钥
     *
     * @param clientDetails 实体
     * @param secret        密钥
     */
    private void setEncodeCLientSecret(ClientDetails clientDetails, String secret) {
        try {
            // MD5加密明文密码，不可解密
            clientDetails.setClientSecret(DigestUtils.md5DigestAsHex(secret.getBytes()));
            // RSA加密明文密码，可解密
            clientDetails.setRawClientSecret(EncryptUtil.encryptForRsaByPublicKey(secret));
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            throw new SecurityException("Operation failed");
        }
    }

    /**
     * 校验表单数据
     *
     * @param dto DTO
     * @throws
     */
    private void validateForm(ClientDetailsDTO dto) {
        // 校验客户端ID，Secret是否合格
        Validator.of(dto)
                 // 校验客户端密钥是否合格
                 .withValidated(data -> this.validateIdAndSecret(data.getClientSecret()),
                         () -> new SecurityException("Secret does not meet the requirements"))
                 // 校验token有效时间范围
                 .withValidated(data -> this.validateTokenTimeScope(data.getAccessTokenValidity()),
                         () -> new SecurityException("The token validity time does not meet the requirements"))
                 // 校验refresh token有效时间范围
                 .withValidated(data -> this.validateRefreshTokenTimeScope(data.getRefreshTokenValidity()),
                         () -> new SecurityException("The refresh token validity time does not meet the requirements"))
                 // 校验认证模式
                 .withValidated(data -> this.ValidateGranterType(data.getAuthorizedGrantTypes()),
                         () -> new SecurityException("The certification model does not meet the requirements"))
                 // 校验权限范围
                 .withValidated(data -> data.getScope().equals(SCOPE),
                         () -> new SecurityException("The scope of authority does not meet the requirements"))
                 // 自动批准权限
                 .withValidated(data -> autoApproveScope.contains(data.getAutoApprove()),
                         () -> new SecurityException("Automatic approval permissions do not meet the requirements"));
    }

    /**
     * 正则校验客户端ID、Secret
     *
     * @param value
     * @return {@link boolean}
     * @throws
     */
    private boolean validateIdAndSecret(String value) {
        return ID_SECRET_PATTERN.matcher(value)
                                .find();
    }

    /**
     * 校验refresh token有效时间范围
     *
     * @param value 值
     * @return {@link boolean}
     * @throws
     */
    private boolean validateRefreshTokenTimeScope(Integer value) {
        if (value != null) {
            return value >= this.tokenRefreshMinTime && value <= this.tokenRefreshMaxTime;
        }
        return false;
    }

    /**
     * 校验token有效时间范围
     *
     * @param value 值
     * @return
     */
    private boolean validateTokenTimeScope(Integer value) {
        if (value != null) {
            return value >= this.tokenMinTime && value <= this.tokenMaxTime;
        }
        return false;
    }

    private boolean ValidateGranterType(String granterTypes) {
        Iterable<String> split = Splitter.on(",")
                                         .trimResults()
                                         .omitEmptyStrings()
                                         .split(granterTypes);
        for (String grantType : split) {
            // 试图匹配系统所有认证模式
            boolean isMatch = false;
            // 循环校验，最终isMatch为false，退出
            for (String type : AuthGranterTypeConstants.ARRAY) {
                // 匹配成功break
                if (type.equals(grantType)) {
                    isMatch = true;
                    break;
                }
            }
            if (!isMatch) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除OAuth客户端
     *
     * @param clientId 客户端id
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void deleteClient(String clientId) {
//        final Session session = SessionManagement.getSession();
//        if (ObjectUtils.nullSafeEquals(clientId, session.getClientId())) {
//            throw new RuntimeException("Prohibit deleting the currently logged in client");
//        }
//        if (!super.removeById(clientId)) {
//            throw new RuntimeException(OAuth2ErrorCodes.INVALID_CLIENT);
//        }
        return null;
    }

    /**
     * 批量删除客户端
     *
     * @param clientIdList 客户端ID列表
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void deleteBatchClient(Set<String> clientIdList) {
//        if (CollectionUtils.isEmpty(clientIdList)) {
//            throw new RuntimeException(OAuth2ErrorCodes.TEMPORARILY_UNAVAILABLE);
//        }
//        final Session session = SessionManagement.getSession();
//        for (String clientId : clientIdList) {
//            if (ObjectUtils.nullSafeEquals(clientId, session.getClientId())) {
//                throw new RuntimeException("Prohibit deleting the currently logged in client");
//            }
//        }
//        if (!super.removeByIds(clientIdList)) {
//            throw new RuntimeException(OAuth2ErrorCodes.INVALID_CLIENT);
//        }
        return null;
    }

}
