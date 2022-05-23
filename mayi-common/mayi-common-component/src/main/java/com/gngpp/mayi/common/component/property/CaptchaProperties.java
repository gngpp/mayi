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

package com.gngpp.mayi.common.component.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码信息配置
 *
 * @author mac
 * Create by Ant on 2020/9/1 上午11:46
 */
@Component
@ConfigurationProperties(prefix = "verify-code")
public class CaptchaProperties {
    /**
     * 验证码有效期/毫秒
     */
    private Long expiration = 60000L;
    /**
     * 验证码内容长度
     */
    private Integer length = 2;
    /**
     * 验证码宽度
     */
    private Integer width = 111;
    /**
     * 验证码高度
     */
    private Integer height = 36;
    /**
     * 验证码字体
     */
    private String fontName;
    /**
     * 字体大小
     */
    private Integer fontSize = 25;
    /**
     * 验证码配置 验证码类型
     */
    private CaptchaTypeEnum codeType;
    /**
     * 验证码 key
     */
    private String keyPrefix;

    public Long getExpiration() {
        return expiration;
    }

    public CaptchaProperties setExpiration(Long expiration) {
        this.expiration = expiration;
        return this;
    }

    public Integer getLength() {
        return length;
    }

    public CaptchaProperties setLength(Integer length) {
        this.length = length;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    public CaptchaProperties setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    public CaptchaProperties setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public String getFontName() {
        return fontName;
    }

    public CaptchaProperties setFontName(String fontName) {
        this.fontName = fontName;
        return this;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public CaptchaProperties setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public CaptchaTypeEnum getCodeType() {
        return codeType;
    }

    public CaptchaProperties setCodeType(CaptchaTypeEnum codeType) {
        this.codeType = codeType;
        return this;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public CaptchaProperties setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
        return this;
    }

    @Override
    public String toString() {
        return "CaptchaProperties{" +
                "expiration=" + expiration +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                ", fontName='" + fontName + '\'' +
                ", fontSize=" + fontSize +
                ", codeType=" + codeType +
                ", keyPrefix='" + keyPrefix + '\'' +
                '}';
    }
}
