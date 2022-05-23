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


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gngpp.mayi.commom.cache.annotation.CacheConfig;
import com.gngpp.mayi.commom.cache.annotation.CacheEvict;
import com.gngpp.mayi.commom.cache.annotation.CachePut;
import com.gngpp.mayi.commom.cache.constants.Namespace;
import com.gngpp.mayi.upms.biz.convert.PositionConvert;
import com.gngpp.mayi.upms.biz.dao.SysPositionDao;
import com.gngpp.mayi.upms.biz.pojo.dto.position.PositionDTO;
import com.gngpp.mayi.upms.biz.pojo.po.SysPosition;
import com.gngpp.mayi.upms.biz.pojo.query.PositionQueryParam;
import com.gngpp.mayi.upms.biz.pojo.query.Query;
import com.gngpp.mayi.upms.biz.pojo.vo.job.PositionExcelVO;
import com.gngpp.mayi.upms.biz.pojo.vo.job.PositionVO;
import com.gngpp.mayi.upms.biz.service.base.AbstractService;
import com.gngpp.mayi.upms.biz.service.exception.PositionException;
import com.gngpp.mayi.upms.biz.service.exception.enums.PositionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 岗位(SysJob)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:45:58
 */
@Service
@CacheConfig(namespace = Namespace.POSITION, dependsOn = Namespace.USER)
@Transactional(readOnly = true, rollbackFor = Throwable.class)
public class SysPositionService extends AbstractService<SysPositionDao, SysPosition> {

    private final Logger log = LoggerFactory.getLogger("[SysPositionService]");
    private final PositionConvert convert = PositionConvert.INSTANCE;

    /**
     * 按条件查询岗位
     *
     * @param query page param
     * @return job list
     */
    @CachePut(key = "#query")
    public IPage<PositionVO> findByQuery(Query<PositionQueryParam> query) {
        IPage<SysPosition> sourcePage = this.queryWrapper()
                                            .chainQuery(query)
                                            .selectPage();
        return super.mapPageToTarget(sourcePage, this.convert::toVo);
    }

    /**
     * 新增岗位
     *
     * @param dto dto
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void saveOne(PositionDTO dto) {
        // 确认职位名是否已经存在
        super.lambdaQuery()
             .eq(SysPosition::getName, dto.getName())
             .oneOpt()
             .ifPresent(position -> {
                 throw new PositionException(PositionState.POSITION_EXISTING, position.getName());
             });
        SysPosition sysPosition = convert.toEntity(dto);
        super.savaOrUpdate(sysPosition);
        return null;
    }

    /**
     * 更新岗位
     *
     * @param dto dto
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void updateOne(PositionDTO dto) {
        // 查询更新岗位是否存在
        final SysPosition sysPosition = super.lambdaQuery()
                                             .eq(SysPosition::getId, dto.getId())
                                             .oneOpt()
                                             .orElseThrow(() -> new PositionException(PositionState.POSITION_NOT_FOUND));
        if (!ObjectUtils.nullSafeEquals(sysPosition.getName(), dto.getName())) {
            // 确认岗位名是否存在
            super.lambdaQuery()
                 .eq(SysPosition::getName, dto.getName())
                 .oneOpt()
                 .ifPresent(var1 -> {
                     throw new PositionException(PositionState.POSITION_EXISTING, var1.getName());
                 });
        }
        // 复制属性
        this.convert.copyProperties(dto, sysPosition);
        // 更新实体
        super.savaOrUpdate(sysPosition);
        return null;
    }

    /**
     * 删除岗位
     *
     * @param ids id collection
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void deleteByIds(Set<Long> ids) {
        super.deleteByIds(ids);
        super.baseMapper.deleteRelationByIds(ids);
        return null;
    }

    /**
     * 下载excel岗位信息
     *
     * @param query request page
     * @param response response
     * @return {@link Void}
     */
    public Void downloadExcel(Query<PositionQueryParam> query, HttpServletResponse response) {
        List<SysPosition> records = super.queryWrapper()
                                         .chainQuery(query)
                                         .selectList();
        List<Map<String,Object>> mapList = new LinkedList<>();
        records.forEach(sysJob -> {
            Map<String, Object> map = new LinkedHashMap<>();
            PositionExcelVO downloadJobVo = new PositionExcelVO();
            downloadJobVo.setName(sysJob.getName());
            downloadJobVo.setEnabled(sysJob.getEnabled());
            downloadJobVo.setCreateBy(sysJob.getCreateBy());
            downloadJobVo.setCreateTime(sysJob.getCreateTime());
            super.setProperties(map,downloadJobVo);
            mapList.add(map);
        });
        super.downloadExcel(mapList, response);
        return null;
    }
}
