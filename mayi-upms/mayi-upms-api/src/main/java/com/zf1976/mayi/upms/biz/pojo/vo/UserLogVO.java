package com.zf1976.mayi.upms.biz.pojo.vo;

import com.zf1976.mayi.upms.biz.pojo.vo.base.AbstractLogVO;

import java.util.Date;

/**
 * @author mac
 * @date 2021/2/2
 **/
public class UserLogVO extends AbstractLogVO {

    /**
     * 描述
     */
    private String description;

    /**
     * ip
     */
    private String ip;

    /**
     * ip来源
     */
    private String ipRegion;

    /**
     * User-Agent
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

    public String getDescription() {
        return description;
    }

    public UserLogVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public UserLogVO setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getIpRegion() {
        return ipRegion;
    }

    public UserLogVO setIpRegion(String ipRegion) {
        this.ipRegion = ipRegion;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public UserLogVO setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public Integer getSpendTime() {
        return spendTime;
    }

    public UserLogVO setSpendTime(Integer spendTime) {
        this.spendTime = spendTime;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public UserLogVO setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public String toString() {
        return "UserLogVO{" +
                "description='" + description + '\'' +
                ", ip='" + ip + '\'' +
                ", ipRegion='" + ipRegion + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", spendTime=" + spendTime +
                ", createTime=" + createTime +
                '}';
    }
}
