package com.zf1976.mayi.common.security.support.session.repository;

import com.zf1976.mayi.common.core.util.RequestUtil;
import com.zf1976.mayi.common.core.util.StringUtil;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.common.security.support.session.Session;
import com.zf1976.mayi.common.security.support.session.exception.SessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * @author mac
 * @date 2021/5/17
 */
public final class RedisSessionRepository extends AbstractSessionRepository {

    private final Logger log = LoggerFactory.getLogger("[SessionManagement]");
    private final HttpClient httpClient = HttpClient.newBuilder()
                                                    .connectTimeout(Duration.ofMillis(1000))
                                                    .version(HttpClient.Version.HTTP_1_1)
                                                    .build();

    public RedisSessionRepository(RedisConnectionFactory connectionFactory) {
        super(connectionFactory);

    }

    public RedisSessionRepository(RedisConnectionFactory connectionFactory,
                                  SecurityProperties properties) {
        super(connectionFactory, properties);
    }

    @Override
    public Optional<Session> getSession() {
        return this.getSession(this.getToken());
    }

    @Override
    public Optional<Session> getSession(String token) {
        Session session = (Session) RequestUtil.getRequest().getAttribute("session");
        if (token != null && session == null) {
            byte[] accessToSessionKey = this.serializeKey(this.getAccessToSessionKey(token));
            try (RedisConnection conn = this.getConnection()) {
                byte[] data = conn.get(accessToSessionKey);
                session = this.deserialize(data);
                RequestUtil.getRequest().setAttribute("session", session);
                return Optional.ofNullable(session);
            }
        }
        return Optional.ofNullable(session);
    }

    @Override
    public Optional<Session> getSession(long sessionId) {
        if (sessionId > 0) {
            byte[] idToSessionKey = this.serializeKey(this.getIdToSessionKey(sessionId));
            try (RedisConnection conn = this.getConnection()) {
                byte[] data = conn.get(idToSessionKey);
                return Optional.ofNullable(this.deserialize(data));
            }
        }
        return Optional.empty();
    }

    @Override
    public void removeSession() {
        this.removeSession(this.getToken());
    }

    @Override
    public void removeSession(String token) {
        if (token != null) {
            String logoutUrl = this.getLogoutUrl();
            HttpRequest request = HttpRequest.newBuilder()
                                             .timeout(Duration.ofMillis(1000))
                                             .uri(URI.create(logoutUrl))
                                             .version(HttpClient.Version.HTTP_1_1)
                                             .POST(HttpRequest.BodyPublishers.noBody())
                                             .header("Authorization", "Bearer " + token)
                                             .build();
            boolean isOk = false;
            try {
                HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    isOk = true;
                }
            } catch (IOException | InterruptedException e) {
                log.error(e.getMessage(), e.getCause());
            }
            if (!isOk) {
                throw new SessionException();
            }
        }
    }

    @Override
    public void removeSession(long sessionId) {
        this.getSession(sessionId)
            .ifPresent(session -> this.removeSession(session.getToken()));
    }

    /**
     * 获取Redis链接
     *
     * @return {@link RedisConnection}
     */
    private RedisConnection getConnection() {
        return RedisConnectionUtils.getConnection(this.connectionFactory);
    }

    /**
     * 获取token
     *
     * @return token
     */
    private String getToken() {
        // 获取请求头
        String header = RequestUtil.getRequest()
                                   .getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null) {
            return header.replace("Bearer ", StringUtil.ENMPTY);
        }
        return null;
    }

}
