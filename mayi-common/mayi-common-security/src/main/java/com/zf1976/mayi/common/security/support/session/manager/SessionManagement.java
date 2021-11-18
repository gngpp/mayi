package com.zf1976.mayi.common.security.support.session.manager;

import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.common.security.support.session.Session;
import com.zf1976.mayi.common.security.support.session.exception.SessionException;
import com.zf1976.mayi.common.security.support.session.repository.AbstractSessionRepository;
import com.zf1976.mayi.common.security.support.session.repository.RedisSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 在同一个操作中需要开启事务 或默认SessionCallable支持，在当前session完成所有操作
 * 身份验证会话管理器 sessionId即为用户id
 *
 * @author mac
 * Create by Ant on 2020/10/4 14:11
 */
@Component
public final class SessionManagement {

    private static final Logger log = LoggerFactory.getLogger("[SessionManagement]");
    private static SecurityProperties properties;
    private static RedisConnectionFactory redisConnectionFactory;
    private static AbstractSessionRepository repository;

    public SessionManagement(RedisConnectionFactory redisConnectionFactory, SecurityProperties properties) {
        SessionManagement.properties = properties;
        SessionManagement.redisConnectionFactory = redisConnectionFactory;
        this.checkStatus();
        repository = new RedisSessionRepository(redisConnectionFactory, properties);
    }

    /**
     * 根据session ID集合查询Session列表
     *
     * @param ids id集合
     * @return {@link List<Session>}
     */
    public static List<Session> selectListByIds(Collection<Long> ids) {
        List<Session> sessions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ids)) {
            ids.forEach(aLong -> sessions.add(getSession(aLong)));

        }
        return sessions;
    }

    /**
     * 获取当前用户名
     *
     * @return {@link String}
     */
    public static String getCurrentUsername() {
        return getSession().getUsername();
    }

    /**
     * 读取当前请求session
     *
     * @return {@link Session}
     * @date 2021-03-23 12:19:55
     */
    public static Session getSession() {
        return repository.getSession()
                         .orElseThrow(SessionException::new);
    }

    /**
     * 根据token获取Session
     *
     * @param token 令牌
     * @return {@link Session}
     */
    public static Session getSession(String token) {
        return repository.getSession(token)
                         .orElseThrow(SessionException::new);
    }

    /**
     * 根据id读取会话
     *
     * @param sessionId session id
     * @return session
     */
    public static Session getSession(Long sessionId) {
        return repository.getSession(sessionId)
                         .orElseThrow(SessionException::new);
    }

    /**
     * 强制当前用户下限
     */
    public static void removeSession() {
        try {
            repository.removeSession();
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
        }
    }

    /**
     * 根据token强制下限
     *
     * @param token 令牌
     */
    public static void removeSession(String token) {
        try {
            repository.removeSession(token);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
        }
    }

    /**
     * 根据id强制下线
     *
     * @param sessionId 用户id
     */
    public static void removeSession(Long sessionId) {
        if (sessionId != null) {
            try {
                repository.removeSession(sessionId);
            } catch (Exception e) {
                log.error(e.getMessage(), e.getCause());
            }
        }
    }

    /**
     * 根据session ID查询session过期时间
     *
     * @param sessionId token
     * @return timestamp
     */
    public static Long getExpiredTime(Long sessionId) {
        return getSession(sessionId).getExpiredTime().toEpochMilli();
    }

    /**
     * 获取当前会话有效期限
     *
     * @return {@link Long}
     */
    public static Long getExpiredTime() {
        return getSession().getExpiredTime().toEpochMilli();
    }

    /**
     * 获取当前会话id
     *
     * @return session
     */
    public static Long getSessionId() {
        return getSession().getId();
    }

    /**
     * owner
     *
     * @return boolean
     */
    public static boolean isOwner() {
        return getSession().getOwner();
    }

    /**
     * 确认必要初始化状态
     */
    private void checkStatus() {
        Assert.notNull(properties, "security properties is null!");
        Assert.notNull(redisConnectionFactory, "redis connection factory is null");
    }

}
