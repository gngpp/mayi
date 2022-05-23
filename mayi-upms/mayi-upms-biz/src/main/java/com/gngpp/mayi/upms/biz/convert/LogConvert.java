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

package com.gngpp.mayi.upms.biz.convert;

import com.gngpp.mayi.upms.biz.pojo.po.SysLog;
import com.gngpp.mayi.upms.biz.pojo.vo.ErrorLogVO;
import com.gngpp.mayi.upms.biz.pojo.vo.LogVO;
import com.gngpp.mayi.upms.biz.pojo.vo.UserLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2021/1/26
 **/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LogConvert {

    LogConvert INSTANCE = Mappers.getMapper(LogConvert.class);

    /**
     * to vo
     *
     * @param sysLog log
     * @return /
     */
    LogVO toVo(SysLog sysLog);

    /**
     * to vo
     *
     * @param sysLog sysLog
     * @return /
     */
    ErrorLogVO toErrorVo(SysLog sysLog);

    /**
     * to vo
     *
     * @param sysLog sysLog
     * @return /
     */
    UserLogVO toUserLogVo(SysLog sysLog);
}
