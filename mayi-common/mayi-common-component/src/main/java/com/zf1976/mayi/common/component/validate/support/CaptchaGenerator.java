/*
 *
 *  * Copyright (c) 2021 zf1976
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

package com.zf1976.mayi.common.component.validate.support;


import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import com.zf1976.mayi.common.component.property.CaptchaProperties;
import com.zf1976.mayi.common.component.property.CaptchaTypeEnum;
import com.zf1976.mayi.common.core.util.SpringContextHolder;
import org.springframework.util.StringUtils;

import java.awt.*;

/**
 * @author mac
 * Create by Ant on 2020/9/1 下午12:37
 */
public class CaptchaGenerator {

    private static CaptchaProperties captchaProperties;

    /**
     * 获取验证码对象
     *
     * @return 验证码
     */
    public static Captcha getCaptcha() {
        if (StringUtils.isEmpty(captchaProperties)) {
            captchaProperties = SpringContextHolder.getBean(CaptchaProperties.class);
            if (StringUtils.isEmpty(captchaProperties.getCodeType())) {
                captchaProperties.setCodeType(CaptchaTypeEnum.ARITHMETIC);
            }
        }
        return generatedCaptcha(captchaProperties);
    }


    /**
     * 根据配置生产验证码
     *
     * @param config 验证码属性
     * @return 验证码
     */
    private static Captcha generatedCaptcha(CaptchaProperties config) {
        Captcha captcha;
        switch (config.getCodeType()) {
            case CHINESE:
                captcha = new ChineseCaptcha(config.getWidth(),
                                             config.getHeight(),
                                             config.getLength());
                break;
            case CHINESE_GIF:
                captcha = new ChineseGifCaptcha(config.getWidth(),
                                                config.getHeight(),
                                                config.getLength());
                break;
            case GIF:
                captcha = new GifCaptcha(config.getWidth(),
                                         config.getHeight(),
                                         config.getLength());
                break;

            case SPEC:
                captcha = new SpecCaptcha(config.getWidth(),
                                          config.getHeight(),
                                          config.getLength());
                break;
            default:
                captcha = new ArithmeticCaptcha(config.getWidth(),
                                                config.getHeight(),
                                                config.getLength());
                break;
        }

        if (config.getFontName() == null || "".equals(config.getFontName())) {
            captcha.setFont(new Font(Font.MONOSPACED,
                                     Font.PLAIN,
                                     config.getFontSize()));
        } else {
            captcha.setFont(new Font(config.getFontName(),
                                     Font.PLAIN,
                                     config.getFontSize()));
        }
        return captcha;
    }
}
