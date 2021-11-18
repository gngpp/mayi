package com.zf1976.mayi.upms.biz.pojo.vo;

import com.zf1976.mayi.upms.biz.pojo.enums.LogType;
import com.zf1976.mayi.upms.biz.pojo.vo.base.AbstractLogVO;

import java.util.Date;

/**
 * @author mac
 * @date 2021/1/26
 **/
public class LogVO extends AbstractLogVO {

    private Long id;

    /**
     * 日志类型
     */
    private LogType logType;

    /**
     * 操作用户
     */
    private String username;

    /**
     * IP地址
     */
    private String ip;

    /**
     * ip来源
     */
    private String ipRegion;

    /**
     * URI
     */
    private String uri;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求参数
     */
    private Object parameter;

    /**
     * 描述
     */
    private String description;

    /**
     * user agent
     */
    private String userAgent;

    /**
     * 消耗时间 /ms
     */
    private Integer spendTime;

    /**
     * 创建时间
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public LogVO setId(Long id) {
        this.id = id;
        return this;
    }

    public LogType getLogType() {
        return logType;
    }

    public LogVO setLogType(LogType logType) {
        this.logType = logType;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public LogVO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public LogVO setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getIpRegion() {
        return ipRegion;
    }

    public LogVO setIpRegion(String ipRegion) {
        this.ipRegion = ipRegion;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public LogVO setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public LogVO setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public Object getParameter() {
        return parameter;
    }

    public LogVO setParameter(Object parameter) {
        this.parameter = parameter;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public LogVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public LogVO setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public Integer getSpendTime() {
        return spendTime;
    }

    public LogVO setSpendTime(Integer spendTime) {
        this.spendTime = spendTime;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public LogVO setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public String toString() {
        return "LogVO{" +
                "id=" + id +
                ", logType=" + logType +
                ", username='" + username + '\'' +
                ", ip='" + ip + '\'' +
                ", ipRegion='" + ipRegion + '\'' +
                ", uri='" + uri + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", parameter=" + parameter +
                ", description='" + description + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", spendTime=" + spendTime +
                ", createTime=" + createTime +
                '}';
    }
}
