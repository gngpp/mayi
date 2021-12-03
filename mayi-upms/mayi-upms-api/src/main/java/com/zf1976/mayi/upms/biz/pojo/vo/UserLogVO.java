/*
 * Copyright (c) 2021 zf1976
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.upms.biz.pojo.vo;

import com.zf1976.mayi.upms.biz.pojo.vo.base.AbstractLogVO;

import java.util.Date;

/**
 * @author mac
 * @date 2021/2/2
 **/
public class UserLogVO extends AbstractLogVO {

    private static final long serialVersionUID = -3439734370718582963L;

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
