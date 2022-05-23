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

package com.gngpp.mayi.upms.biz.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mac
 */
@Component
@ConfigurationProperties(prefix = "email")
public class EmailProperties {

    /**
     * 健前缀
     */
    private String keyPrefix;
    /**
     * 字体长度
     */
    private Integer length;
    /**
     * 时间
     */
    private Long expired;
    /**
     * 收件人
     */
    private String name;
    /**
     * 主体
     */
    private String subject;

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public EmailProperties setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
        return this;
    }

    public Integer getLength() {
        return length;
    }

    public EmailProperties setLength(Integer length) {
        this.length = length;
        return this;
    }

    public Long getExpired() {
        return expired;
    }

    public EmailProperties setExpired(Long expired) {
        this.expired = expired;
        return this;
    }

    public String getName() {
        return name;
    }

    public EmailProperties setName(String name) {
        this.name = name;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public EmailProperties setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    @Override
    public String toString() {
        return "ValidateProperties{" +
                "keyPrefix='" + keyPrefix + '\'' +
                ", length=" + length +
                ", expired=" + expired +
                ", name='" + name + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
