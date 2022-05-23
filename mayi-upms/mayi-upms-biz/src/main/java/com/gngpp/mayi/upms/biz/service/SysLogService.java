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

package com.gngpp.mayi.upms.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gngpp.mayi.common.core.foundation.exception.BusinessException;
import com.gngpp.mayi.common.core.foundation.exception.BusinessMsgState;
import com.gngpp.mayi.upms.biz.convert.LogConvert;
import com.gngpp.mayi.upms.biz.dao.SysLogDao;
import com.gngpp.mayi.upms.biz.pojo.enums.LogType;
import com.gngpp.mayi.upms.biz.pojo.po.SysLog;
import com.gngpp.mayi.upms.biz.pojo.query.LogQueryParam;
import com.gngpp.mayi.upms.biz.pojo.query.Query;
import com.gngpp.mayi.upms.biz.pojo.vo.base.AbstractLogVO;
import com.gngpp.mayi.upms.biz.security.Context;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/1/25
 **/
@Service
@Transactional(readOnly = true, rollbackFor = Throwable.class)
public class SysLogService extends ServiceImpl<SysLogDao, SysLog> {

    private final LogConvert convert = LogConvert.INSTANCE;

    public IPage<AbstractLogVO> findByQueryForUser(Query<LogQueryParam> query) {
        LogQueryParam param = query.getQuery();
        Assert.notNull(param, BusinessMsgState.PARAM_ILLEGAL::getReasonPhrase);
        // 查询分页对象
        Page<SysLog> sourcePage = super.lambdaQuery()
                                       .eq(SysLog::getLogType, LogType.INFO)
                                       .eq(SysLog::getUsername, Context.username())
                                       .page(new Page<>(query.getPage(), query.getSize()));
        return this.mapPage(sourcePage,convert::toUserLogVo);
    }

    public IPage<AbstractLogVO> findByQuery(Query<LogQueryParam> query) {
        LogQueryParam param = query.getQuery();
        Assert.notNull(param, BusinessMsgState.PARAM_ILLEGAL::getReasonPhrase);
        if (param.getLogType() == null) {
            throw new BusinessException(BusinessMsgState.PARAM_ILLEGAL);
        }
        // 构建分页对象
        Page<SysLog> page = new Page<>(query.getPage(), query.getSize());
        // 构造查询条件
        byte range = 2;
        Page<SysLog> sourcePage;
        LambdaQueryChainWrapper<SysLog> lambdaQuery = super.lambdaQuery();
        if (param.getCreateTime() != null) {
            List<Date> createTime = param.getCreateTime();
            if (createTime.size() == range) {
                lambdaQuery.between(SysLog::getCreateTime,
                        createTime.get(0),
                        createTime.get(1));
            } else {
                throw new BusinessException(BusinessMsgState.PARAM_ILLEGAL);
            }
        }
        // 分页查询
        sourcePage = lambdaQuery.eq(SysLog::getLogType, param.getLogType())
                                .page(page);
        IPage<AbstractLogVO> targetPage = null;
        switch (param.getLogType()) {
            case INFO -> targetPage = this.mapPage(sourcePage, convert::toVo);
            case ERROR -> targetPage = this.mapPage(sourcePage, convert::toErrorVo);
            default -> {
            }
        }
        // 进行过滤
        if (!ObjectUtils.isEmpty(targetPage) && param.getBlurry() != null) {
            List<AbstractLogVO> targetRecords = targetPage.getRecords()
                                                    .stream()
                                                    .filter(abstractLogVO -> {
                                                        if (param.getBlurry() != null) {
                                                            return this.getKeyword(abstractLogVO)
                                                                       .contains(param.getBlurry());
                                                        }
                                                        return false;
                                                    })
                                                    .collect(Collectors.toList());
            return targetPage.setRecords(targetRecords);
        }
        return targetPage;
    }

    /**
     * 分页对象拷贝
     *
     * @param sourcePage 原对象
     * @param translator func
     * @param <S>        目标对象
     * @return 转换结果
     */
    private  <S> IPage<S> mapPage(IPage<SysLog> sourcePage, Function<SysLog, S> translator) {
        List<S> target = sourcePage.getRecords()
                                   .stream()
                                   .map(translator)
                                   .collect(Collectors.toList());

        final IPage<S> targetPage = new Page<>(sourcePage.getCurrent(),
                sourcePage.getSize(),
                sourcePage.getTotal(),
                sourcePage.isSearchCount());
        return targetPage.setRecords(target);
    }

    private String getKeyword(AbstractLogVO vo) {
        final StringBuilder builder = new StringBuilder();
        for (Field field : vo.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            builder.append(ReflectionUtils.getField(field, vo));
        }
        return builder.toString();
    }

    /**
     * 删除日志
     *
     * @param ids ids
     * @return {@link Void}
     */
    @Transactional
    public Void deleteByIds(Set<Long> ids) {
        if (!super.removeByIds(ids)) {
            throw new BusinessException(BusinessMsgState.OPT_ERROR);
        }
        return null;
    }

    /**
     * 删除所有错误日志
     *
     * @return {@link Void}
     */
    @Transactional
    public Void deleteError() {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<SysLog>()
                .eq(SysLog::getLogType, LogType.ERROR);
        if (!super.remove(wrapper)) {
            throw new BusinessException(BusinessMsgState.OPT_ERROR);
        }
        return null;
    }

    /**
     * 删除所有常规日志
     *
     * @return {@link Void}
     */
    @Transactional
    public Void deleteInfo() {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<SysLog>()
                .ne(SysLog::getLogType, LogType.ERROR);
        if (!super.remove(wrapper)) {
            throw new BusinessException(BusinessMsgState.OPT_ERROR);
        }
        return null;
    }

}
