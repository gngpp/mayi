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

package com.zf1976.mayi.upms.biz.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.zf1976.mayi.common.component.cache.annotation.CacheConfig;
import com.zf1976.mayi.common.component.cache.annotation.CacheEvict;
import com.zf1976.mayi.common.component.cache.annotation.CachePut;
import com.zf1976.mayi.common.core.constants.Namespace;
import com.zf1976.mayi.upms.biz.convert.DictConvert;
import com.zf1976.mayi.upms.biz.dao.SysDictDao;
import com.zf1976.mayi.upms.biz.dao.SysDictDetailDao;
import com.zf1976.mayi.upms.biz.pojo.dto.dict.DictDTO;
import com.zf1976.mayi.upms.biz.pojo.po.SysDict;
import com.zf1976.mayi.upms.biz.pojo.po.SysDictDetail;
import com.zf1976.mayi.upms.biz.pojo.query.DictQueryParam;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.vo.dict.DictDownloadVO;
import com.zf1976.mayi.upms.biz.pojo.vo.dict.DictVO;
import com.zf1976.mayi.upms.biz.service.base.AbstractService;
import com.zf1976.mayi.upms.biz.service.exception.DictException;
import com.zf1976.mayi.upms.biz.service.exception.enums.DictState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 数据字典(SysDict)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:45:59
 */
@Service
@CacheConfig(namespace = Namespace.DICT)
@Transactional(rollbackFor = Throwable.class)
public class SysDictService extends AbstractService<SysDictDao, SysDict> {

    private final Logger log = LoggerFactory.getLogger("[SysDictService]");
    private final SysDictDetailDao sysDictDetailDao;
    private final DictConvert convert = DictConvert.INSTANCE;

    public SysDictService(SysDictDetailDao sysDictDetailDao) {
        this.sysDictDetailDao = sysDictDetailDao;
    }

    /**
     * 按条件查询字典页
     *
     * @param query query param
     * @return dict list
     */
    @CachePut(key = "#query")
    public IPage<DictVO> findByQuery(Query<DictQueryParam> query) {
        IPage<SysDict> sourcePage = super.queryWrapper()
                                         .chainQuery(query)
                                         .selectPage();
        return super.mapPageToTarget(sourcePage, this.convert::toVo);
    }

    /**
     * 新增字典
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional
    public Void saveOne(DictDTO dto) {
        // 确认字典名是否存在
        super.lambdaQuery()
             .eq(SysDict::getDictName, dto.getDictName())
             .oneOpt()
             .ifPresent(sysDict -> {
                 throw new DictException(DictState.DICT_EXISTING, sysDict.getDictName());
             });
        SysDict sysDict = convert.toEntity(dto);
        super.savaOrUpdate(sysDict);
        return null;
    }

    /**
     * 更新字典
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional
    public Void updateOne(DictDTO dto) {

        // 查询字典实体
        SysDict sysDict = super.lambdaQuery()
                               .eq(SysDict::getId, dto.getId())
                               .oneOpt().orElseThrow(() -> new DictException(DictState.DICT_NOT_FOUND));

        if (!ObjectUtils.nullSafeEquals(dto.getDictName(), sysDict.getDictName())) {
            // 确认字典名是否已存在
            super.lambdaQuery()
                 .eq(SysDict::getDictName, dto.getDictName())
                 .oneOpt()
                 .ifPresent(var1 -> {
                     throw new DictException(DictState.DICT_EXISTING, var1.getDictName());
                 });
        }
        // 复制属性
        this.convert.copyProperties(dto, sysDict);
        // 更新实体
        super.savaOrUpdate(sysDict);
        return null;
    }

    /**
     * 删除字典
     *
     * @param ids id collection
     * @return /
     */
    @CacheEvict
    @Transactional
    public Void deleteByIds(Set<Long> ids) {
        super.deleteByIds(ids);
        return null;
    }

    /**
     * 下载dict excel文件
     * @param query query param
     * @param response response
     * @return /
     */
    @Transactional(readOnly = true)
    public Void downloadExcel(Query<DictQueryParam> query, HttpServletResponse response) {
        List<SysDict> records = super.queryWrapper()
                                     .chainQuery(query)
                                     .selectList();
        List<Map<String,Object>> mapList = new LinkedList<>();
        records.forEach(sysDict -> {
            List<SysDictDetail> details = ChainWrappers.lambdaQueryChain(sysDictDetailDao)
                                                       .eq(SysDictDetail::getDictId, sysDict.getId())
                                                       .list();
            details.forEach(sysDictDetail -> {
                DictDownloadVO downloadDictVo = new DictDownloadVO();
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                downloadDictVo.setDictName(sysDict.getDictName());
                downloadDictVo.setDescription(sysDict.getDescription());
                downloadDictVo.setLabel(sysDictDetail.getLabel());
                downloadDictVo.setValue(sysDictDetail.getValue());
                downloadDictVo.setCreateBy(sysDict.getCreateBy());
                downloadDictVo.setCreateTime(sysDict.getCreateTime());
                super.setProperties(map,downloadDictVo);
                mapList.add(map);
            });
        });
        super.downloadExcel(mapList,response);
        return null;
    }
}
