package com.zf1976.mayi.upms.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.mayi.common.core.util.RedisUtil;
import com.zf1976.mayi.common.core.util.StringUtil;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.common.security.support.session.Session;
import com.zf1976.mayi.common.security.support.session.manager.SessionManagement;
import com.zf1976.mayi.upms.biz.convert.SessionConvert;
import com.zf1976.mayi.upms.biz.feign.RemoteAuthClient;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.query.SessionQueryParam;
import com.zf1976.mayi.upms.biz.pojo.vo.SessionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/1/20
 **/
@Service
public class SysOnlineService {

    private final Logger log = LoggerFactory.getLogger("[SysOnlineService]");
    private final SecurityProperties securityProperties;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final static String PATTERN_SUFFIX = "*";
    private final SessionConvert sessionConvert = SessionConvert.INSTANCE;
    private final RestTemplate restTemplate = new RestTemplate();
    private final RemoteAuthClient securityClient;
    private ClientHttpRequest clientHttpRequest;

    public SysOnlineService(RedisTemplate<Object, Object> template, SecurityProperties securityProperties, RemoteAuthClient securityClient) {
        this.redisTemplate = template;
        this.securityProperties = securityProperties;
        this.securityClient = securityClient;
        this.checkState();
    }

    private void checkState() {
        Assert.notNull(redisTemplate, "repository cannot be null");
        Assert.notNull(securityProperties, "config cannot be null");
    }


    public IPage<SessionVO> selectSessionPage(Query<SessionQueryParam> query){
        final Set<Long> sessionIdList = this.getOnlinePageSessionIds(query);
        final SessionQueryParam param = query.getQuery();
        Assert.notNull(param, BusinessMsgState.PARAM_ILLEGAL::getReasonPhrase);
        final List<SessionVO> vos = SessionManagement.selectListByIds(sessionIdList)
                                                     .stream()
                                                     .map(sessionConvert::toVO)
                                                     .filter(vo -> {
                                                                 if (param.getFilter() != null) {
                                                                     return this.getKeyword(vo)
                                                                                .contains(param.getFilter());
                                                                 }
                                                                 return true;
                                                             }).collect(Collectors.toList());
        final Page<SessionVO> page = new Page<>(query.getPage(), query.getSize(), vos.size());
        return page.setRecords(vos);
    }

    public Set<Long> getOnlinePageSessionIds() {
        return RedisUtil.scanKeys(this.getPatternSessionId())
                        .stream()
                        .map(StringUtil::getNumber)
                        .collect(Collectors.toSet());
    }

    public Set<Long> getOnlinePageSessionIds(Query<SessionQueryParam> query) {
        final int page = query.getPage();
        final int size = query.getSize();
        return RedisUtil.scanKeysForPage(this.getPatternSessionId(), page, size)
                        .stream()
                        .map(StringUtil::getNumber)
                        .collect(Collectors.toSet());
    }

    private String getKeyword(SessionVO vo) {
        final StringBuilder builder = new StringBuilder();
        for (Field field : vo.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            builder.append(ReflectionUtils.getField(field, vo));
        }
        return builder.toString();
    }

    private String getPatternSessionId() {
        return securityProperties.getPrefixSessionId() + PATTERN_SUFFIX;
    }

    private ClientHttpRequest createLogoutRequest() {
        if (this.clientHttpRequest == null) {
            synchronized (this) {
                if (this.clientHttpRequest == null) {
                    var requestFactory = this.restTemplate.getRequestFactory();
                    try {
                        this.clientHttpRequest = requestFactory.createRequest(URI.create(securityProperties.getLogoutUrl()),HttpMethod.POST);
                    } catch (IOException e) {
                        log.error("create revoke request client fail：{}", e.getMessage());
                    }
                }
            }
        }
        return this.clientHttpRequest;
    }

    /**
     * 执行登出处理
     *
     * @date 2021-05-07 11:32:45
     * @param token 令牌
     * @return {@link boolean}
     */
    private boolean executeLogout(String token) {
        var logoutRequest = createLogoutRequest();
        logoutRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, token);
        try {
            var response = logoutRequest.execute();
            return response.getStatusCode().is2xxSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Void forceOffline(Set<Long> ids) {
        // 操作方
        Long sessionId = SessionManagement.getSessionId();
        ids.forEach(id -> {
            // 不允许强制自己离线
            if (!ObjectUtils.nullSafeEquals(id, sessionId)){
                // 调用远程服务进行登出处理
                @SuppressWarnings("rawtypes")
                DataResult logoutResult;
                Session forceSession = SessionManagement.getSession(id);
                final String token = this.formatToken(forceSession.getToken());
                logoutResult = this.securityClient.logout(token);
                Assert.isTrue(logoutResult != null && logoutResult.getSuccess(),"this session not online");
            }
        });
        return null;
    }

    private String formatToken(String tokenValue) {
        return "Bearer " + tokenValue;
    }
}
