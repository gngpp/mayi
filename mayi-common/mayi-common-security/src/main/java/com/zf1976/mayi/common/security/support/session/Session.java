package com.zf1976.mayi.common.security.support.session;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mac
 * Create by Ant on 2020/9/28 23:35
 */
@SuppressWarnings("UnusedReturnValue")
public class Session implements Serializable {

    private static final long serialVersionUID = 6342241044575234092L;

    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 登录客户端id
     */
    private String clientId;
    /**
     * 资源所有者
     */
    private Boolean owner = Boolean.FALSE;
    /**
     * 浏览器
     */
    private String browser;
    /**
     * 操作系统类型
     */
    private String operatingSystemType;
    /**
     * IP
     */
    private String ip;
    /**
     * 地址
     */
    private String ipRegion;
    /**
     * token
     */
    private String token;
    /**
     * 权限
     */
    private Collection<String> permissions;
    /**
     * 登录时间
     */
    private Instant loginTime;
    /**
     * 到期时间
     */
    private Instant expiredTime;

    private final Map<Object, Object> attribute = new HashMap<>();


    public Long getId() {
        return id;
    }

    public Session setId(Long id) {
        this.id = id;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public Session setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Session setUsername(String username) {
        this.username = username;
        return this;
    }

    public Collection<String> getPermissions() {
        return permissions;
    }

    public Session setPermissions(Collection<String> permissions) {
        this.permissions = permissions;
        return this;
    }

    public Boolean getOwner() {
        return owner;
    }

    public Session setOwner(Boolean owner) {
        this.owner = owner;
        return this;
    }

    public String getBrowser() {
        return browser;
    }

    public Session setBrowser(String browser) {
        this.browser = browser;
        return this;
    }

    public String getOperatingSystemType() {
        return operatingSystemType;
    }

    public Session setOperatingSystemType(String operatingSystemType) {
        this.operatingSystemType = operatingSystemType;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public Session setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getIpRegion() {
        return ipRegion;
    }

    public Session setIpRegion(String ipRegion) {
        this.ipRegion = ipRegion;
        return this;
    }

    public String getToken() {
        return token;
    }

    public Session setToken(String token) {
        this.token = token;
        return this;
    }

    public Instant getLoginTime() {
        return loginTime;
    }

    public Session setLoginTime(Instant loginTime) {
        this.loginTime = loginTime;
        return this;
    }

    public Map<Object, Object> getAttribute() {
        return attribute;
    }

    public void setAttribute(Object key, Object value) {
        this.attribute.put(key, value);
    }

    public void removeAttribute(Object key) {
        this.attribute.remove(key);
    }

    public Object getAttribute(Object key) {
        return this.attribute.get(key);
    }

    public Instant getExpiredTime() {
        return expiredTime;
    }

    public Session setExpiredTime(Instant expiredTime) {
        this.expiredTime = expiredTime;
        return this;
    }


    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", clientId='" + clientId + '\'' +
                ", owner=" + owner +
                ", browser='" + browser + '\'' +
                ", operatingSystemType='" + operatingSystemType + '\'' +
                ", ip='" + ip + '\'' +
                ", ipRegion='" + ipRegion + '\'' +
                ", token='" + token + '\'' +
                ", permissions=" + permissions +
                ", loginTime=" + loginTime +
                ", expiredTime=" + expiredTime +
                ", attribute=" + attribute +
                '}';
    }

    public static final class SessionBuilder {
        private Long id;
        private String username;
        private String clientId;
        private Boolean owner = Boolean.FALSE;
        private String browser;
        private String operatingSystemType;
        private String ip;
        private String ipRegion;
        private String token;
        private Collection<String> permissions;
        private Instant loginTime;
        private Instant expiredTime;
        private Map<Object, Object> attribute = new HashMap<>();

        private SessionBuilder() {
        }

        public static SessionBuilder newBuilder() {
            return new SessionBuilder();
        }

        public SessionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SessionBuilder username(String username) {
            this.username = username;
            return this;
        }

        public SessionBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public SessionBuilder owner(Boolean owner) {
            this.owner = owner;
            return this;
        }

        public SessionBuilder browser(String browser) {
            this.browser = browser;
            return this;
        }

        public SessionBuilder operatingSystemType(String operatingSystemType) {
            this.operatingSystemType = operatingSystemType;
            return this;
        }

        public SessionBuilder ip(String ip) {
            this.ip = ip;
            return this;
        }

        public SessionBuilder ipRegion(String ipRegion) {
            this.ipRegion = ipRegion;
            return this;
        }

        public SessionBuilder token(String token) {
            this.token = token;
            return this;
        }

        public SessionBuilder permissions(Collection<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public SessionBuilder loginTime(Instant loginTime) {
            this.loginTime = loginTime;
            return this;
        }

        public SessionBuilder expiredTime(Instant expiredTime) {
            this.expiredTime = expiredTime;
            return this;
        }

        public SessionBuilder attribute(Map<Object, Object> attribute) {
            this.attribute = attribute;
            return this;
        }

        public Session build() {
            Session session = new Session();
            session.setId(id);
            session.setUsername(username);
            session.setClientId(clientId);
            session.setOwner(owner);
            session.setBrowser(browser);
            session.setOperatingSystemType(operatingSystemType);
            session.setIp(ip);
            session.setIpRegion(ipRegion);
            session.setToken(token);
            session.setPermissions(permissions);
            session.setLoginTime(loginTime);
            session.setExpiredTime(expiredTime);
            attribute.forEach(session::setAttribute);
            return session;
        }
    }
}
