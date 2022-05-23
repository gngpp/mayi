/*
 *
 *  * Copyright (c) 2021 gngpp
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *  *
 *
 */

package com.gngpp.mayi.upms.biz.pojo.vo;

import com.gngpp.mayi.upms.biz.pojo.enums.LogType;
import com.gngpp.mayi.upms.biz.pojo.vo.base.AbstractLogVO;

import java.util.Date;

/**
 * @author mac
 * @date 2021/1/26
 **/
public class ErrorLogVO extends AbstractLogVO {

    private static final long serialVersionUID = -4465284404065856817L;

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
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 异常细节
     */
    private String exceptionDetails;

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

    public ErrorLogVO setId(Long id) {
        this.id = id;
        return this;
    }

    public LogType getLogType() {
        return logType;
    }

    public ErrorLogVO setLogType(LogType logType) {
        this.logType = logType;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public ErrorLogVO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public ErrorLogVO setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getIpRegion() {
        return ipRegion;
    }

    public ErrorLogVO setIpRegion(String ipRegion) {
        this.ipRegion = ipRegion;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public ErrorLogVO setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public ErrorLogVO setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public Object getParameter() {
        return parameter;
    }

    public ErrorLogVO setParameter(Object parameter) {
        this.parameter = parameter;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ErrorLogVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public ErrorLogVO setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public ErrorLogVO setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public ErrorLogVO setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getExceptionDetails() {
        return exceptionDetails;
    }

    public ErrorLogVO setExceptionDetails(String exceptionDetails) {
        this.exceptionDetails = exceptionDetails;
        return this;
    }

    public Integer getSpendTime() {
        return spendTime;
    }

    public ErrorLogVO setSpendTime(Integer spendTime) {
        this.spendTime = spendTime;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public ErrorLogVO setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public String toString() {
        return "ErrorLogVO{" +
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
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", exceptionDetails='" + exceptionDetails + '\'' +
                ", spendTime=" + spendTime +
                ", createTime=" + createTime +
                '}';
    }
}
