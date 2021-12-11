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

package com.zf1976.mayi.upms.biz.service.base;

import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.zf1976.mayi.common.core.foundation.exception.BusinessException;
import com.zf1976.mayi.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.mayi.common.core.util.StringUtil;
import com.zf1976.mayi.upms.biz.pojo.query.AbstractQueryParam;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.query.annotation.Param;
import com.zf1976.mayi.upms.biz.service.util.LambdaMethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author mac
 * Create by Ant on 2020/8/31 上午11:37
 */

public abstract class AbstractService<D extends BaseMapper<E>, E> extends ServiceImpl<D, E> {

    protected final static AlternativeJdkIdGenerator JDK_ID_GENERATOR = new AlternativeJdkIdGenerator();
    protected final static String SYS_TEM_DIR = System.getProperty("java.io.tmpdir") + File.separator;
    protected final Logger log = LoggerFactory.getLogger(AbstractService.class);
    protected final ThreadLocal<QueryChainWrapper<E>> wrapperThreadLocal = new ThreadLocal<>();
    protected final ThreadLocal<Query<? extends AbstractQueryParam>> queryThreadLocal = new ThreadLocal<>();
    protected static final int MAX_PAGE_DEPARTMENT = 9999;

    public AbstractService() {
        this.removeThreadLocalVariable();
    }

    /**
     * 确实是否保存ThreadLocal 变量
     */
    private void checkThreadLocalVariable() {
        Assert.notNull(this.wrapperThreadLocal.get(), "queryChainWrapperThreadLocal failed to initialize");
        Assert.notNull(this.queryThreadLocal.get(), "requestPageThreadLocal failed to initialize");
    }

    /**
     * 删除ThreadLocal 保存变量
     */
    private void removeThreadLocalVariable() {
        this.queryThreadLocal.remove();
        this.wrapperThreadLocal.remove();
    }

    /**
     * 根据id集合取回对象列表
     *
     * @param idList id集合
     * @return 对象列表
     */
    @Override
    public List<E> listByIds(Collection<? extends Serializable> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return baseMapper.selectBatchIds(idList);
    }

    /**
     * 分页对象拷贝
     *
     * @param sourcePage 原对象
     * @param translator func
     * @param <S>        目标对象
     * @return 转换结果
     */
    protected <S> IPage<S> mapPageToTarget(IPage<E> sourcePage, Function<E, S> translator) {
        final List<S> target = sourcePage.getRecords()
                                         .stream()
                                         .map(translator)
                                         .collect(Collectors.toList());

        final Page<S> targetPage = new Page<>(sourcePage.getCurrent(),
                                              sourcePage.getSize(),
                                              sourcePage.getTotal(),
                                              sourcePage.isSearchCount());
        return targetPage.setRecords(target);
    }

    /**
     * 列表对象拷贝
     *
     * @param sourceList 原对象
     * @param translator func
     * @param <S>        目标对象
     * @return 转换结果
     */
    protected <S> List<S> mapListToTarget(Collection<E> sourceList, Function<E, S> translator) {
        return sourceList.stream()
                         .map(translator)
                         .collect(Collectors.toList());
    }

    /**
     * 获取行字段
     *
     * @param column column mapper
     * @return field
     */
    protected String getColumn(SFunction<E, ?> column) {
        return LambdaMethodUtils.columnToString(column);
    }

    /**
     * 链式 wrapper 查询
     *
     * @return this
     */
    protected AbstractService<D, E> queryWrapper() {
        return this.queryWrapper(ChainWrappers.queryChain(super.baseMapper));
    }

    /**
     * 链式 wrapper 查询
     *
     * @param queryChainWrapper wrapper
     * @return {@link AbstractService<D,E>}
     */

    protected AbstractService<D, E> queryWrapper(QueryChainWrapper<E> queryChainWrapper) {
        this.wrapperThreadLocal.set(queryChainWrapper);
        return this;
    }

    /**
     * 分页查询
     *
     * @return page entity
     */
    public IPage<E> selectPage() {
        // 查询对象参数
        Query<? extends AbstractQueryParam> query = Optional.ofNullable(this.queryThreadLocal.get())
                                                            .orElseGet(Query::new);
        // 查询条件
        QueryChainWrapper<E> queryChainWrapper = Optional.ofNullable(this.wrapperThreadLocal.get())
                                                         .orElseGet(() -> ChainWrappers.queryChain(super.baseMapper));
        // 清除本地变量
        this.removeThreadLocalVariable();
        // 分页查询
        return this.selectPage(query, queryChainWrapper);
    }

    /**
     * 无条件查询
     *
     * @return list of Entity
     */
    public List<E> selectList() {
        // List query
        List<E> list = Optional.ofNullable(this.wrapperThreadLocal.get())
                               .orElseGet(() -> ChainWrappers.queryChain(super.baseMapper))
                               .list();
        // Clear local variables
        this.removeThreadLocalVariable();
        return list;
    }

    /**
     * 获取查询页
     *
     * @param query request page param
     * @return page
     */
    public IPage<E> selectPage(Query<? extends AbstractQueryParam> query, QueryChainWrapper<E> queryChainWrapper) {
        // Clear local variables
        this.removeThreadLocalVariable();
        Page<E> configPage = this.getPage(query);
        return queryChainWrapper.page(configPage);
    }

    /**
     * 获取查询页
     *
     * @param query request page param
     * @return page
     */
    public IPage<E> selectPage(Query<? extends AbstractQueryParam> query,
                               LambdaQueryChainWrapper<E> lambdaQueryChainWrapper) {
        // Clear local variables
        this.removeThreadLocalVariable();
        Page<E> configPage = this.getPage(query);
        return lambdaQueryChainWrapper.page(configPage);
    }

    /**
     * 分页配置
     *
     * @param query 查询参数
     */
    protected Page<E> getPage(Query<? extends AbstractQueryParam> query) {
        Assert.notNull(query, BusinessMsgState.PARAM_ILLEGAL.getReasonPhrase());
        Page<E> configPage = new Page<>();
        configPage.setOrders(query.getOrders());
        return configPage.setCurrent(query.getPage())
                         .setSize(query.getSize());
    }

    /**
     * 默认分页配置
     */
    protected Page<E> getPage() {
        return this.getPage(new Query<>());
    }

    /**
     * 设置属性
     *
     * @param map map
     * @param obj obj
     * @param <T> 待确定类型
     */
    protected <T> void setProperties(Map<String, Object> map, T obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
                throw new BusinessException(BusinessMsgState.DOWNLOAD_ERROR);
            }
        }
    }

    /**
     * 链式查询 设置查询参数
     ** @param query 查询参数
     * @return this
     */
    public AbstractService<D, E> chainQuery(Query<? extends AbstractQueryParam> query) {
        // Set local query variables
        this.queryThreadLocal.set(query);
        return this.chainQuery(query, () -> null);
    }

    /**
     * 链式查询 设置查询参数 / 暂时支持这么多, between 只支持时间类型
     *
     * @param query 查询参数
     * @return this
     */
    public AbstractService<D, E> chainQuery(Query<? extends AbstractQueryParam> query, Supplier<QueryChainWrapper<E>> supplier) {
        Assert.notNull(query, BusinessMsgState.PARAM_ILLEGAL.getReasonPhrase());
        AbstractQueryParam param = query.getQuery();
        if (param == null) {
            return this;
        }
        this.queryThreadLocal.set(query);
        QueryChainWrapper<E> supplierWrapper = supplier.get();
        if (supplierWrapper == null) {
            supplierWrapper = wrapperThreadLocal.get();
            // 校验是否初始化
            this.checkThreadLocalVariable();
        } else {
            // 自定义wrapper
            this.wrapperThreadLocal.set(supplierWrapper);
        }
        for (Field field : param.getClass().getDeclaredFields()) {
            ReflectionUtils.makeAccessible(field);
            String fieldName = StringUtil.camelToUnderline(field.getName());
            Object fieldVal = ReflectionUtils.getField(field, param);
            Param annotation = field.getAnnotation(Param.class);
            // 空值字段直接跳过
            if (ObjectUtils.isEmpty(fieldVal) || annotation == null) {
                continue;
            }
            switch (annotation.type()) {
                case EQ:
                    supplierWrapper.eq(fieldName, fieldVal);
                    break;
                case NE:
                    supplierWrapper.ne(fieldName, fieldVal);
                    break;
                case IN:
                    if (fieldVal instanceof Collection) {
                        supplierWrapper.in(fieldName, (Collection<?>) fieldVal);
                    } else {
                        supplierWrapper.in(fieldName, fieldVal);
                    }
                    break;
                case LIKE:
                    String[] fields = field.getAnnotation(Param.class).fields();
                    if (ObjectUtils.isEmpty(fields)) {
                        supplierWrapper.like(fieldName, fieldVal);
                    } else {
                        for (int i = 0; i < fields.length; i++) {
                            if (i == fields.length - 1) {
                                supplierWrapper.like(StringUtil.camelToUnderline(fields[i]), fieldVal);
                            } else {
                                supplierWrapper.like(StringUtil.camelToUnderline(fields[i]), fieldVal)
                                               .or();
                            }
                        }
                    }
                    break;
                case NOT_LIKE:
                    supplierWrapper.notLike(fieldName, fieldVal);
                    break;
                case BETWEEN:
                    @SuppressWarnings("unchecked")
                    List<Date> list = (List<Date>) fieldVal;
                    Assert.isTrue(list.size() == 2, "Must be a time range");
                    supplierWrapper.between(fieldName, list.get(0), list.get(1));
                    break;
                default:
                    break;
            }
        }
        return this;
    }

    /**
     * 导出excel
     *
     * @param list     对象list
     * @param response response
     */
    protected void downloadExcel(Collection<Map<String, Object>> list, HttpServletResponse response) {
        String tempPath = SYS_TEM_DIR + JDK_ID_GENERATOR.generateId() + ".xlsx";
        File file = new File(tempPath);
        try (ServletOutputStream out = response.getOutputStream();
             BigExcelWriter writer = ExcelUtil.getBigWriter(file)) {
            // 临时目录
            writer.write(list, true);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=file.xlsx");
            file.deleteOnExit();
            writer.flush(out);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(BusinessMsgState.OPT_ERROR);
        }
    }

    /**
     * 验证字段是否为空
     *
     * @param param request param
     * @return boolean
     */
    protected Boolean checkAllFieldIsNotNull(AbstractQueryParam param){
        Assert.notNull(param, BusinessMsgState.PARAM_ILLEGAL::getReasonPhrase);
        for (Field field : param.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (!ObjectUtils.isEmpty(ReflectionUtils.getField(field, param))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验相通字段值
     *
     * @param entity entity
     * @param dto dto
     * @param collectionHandler state
     */
    protected void validateFields(E entity, Object dto, Consumer<Collection<Object>> collectionHandler) {
        final Set<Object> collectState = new CopyOnWriteArraySet<>();
        ReflectionUtils.doWithLocalFields(entity.getClass(), entityField -> {
            entityField.setAccessible(true);
            Object entityFieldVal = ReflectionUtils.getField(entityField, entity);
            if (!ObjectUtils.isEmpty(entityFieldVal)) {
                ReflectionUtils.doWithLocalFields(dto.getClass(), dtoField -> {
                    dtoField.setAccessible(true);
                    Object dtoFieldVal = ReflectionUtils.getField(dtoField, dto);
                    if (ObjectUtils.nullSafeEquals(entityField.getType(),dtoField.getType())
                            && !ObjectUtils.isEmpty(dtoFieldVal)
                            && ObjectUtils.nullSafeEquals(entityField.getName(), dtoField.getName())
                            && ObjectUtils.nullSafeEquals(entityFieldVal, dtoFieldVal)
                    ) {
                        collectState.add(entityFieldVal);
                    }
                });
            }
        });
        collectionHandler.accept(collectState);
    }

    /**
     * 根据实体插入或更新,只有存在id情况才会执行更新，否则执行插入
     *
     * @param entity entity
     */
    protected void savaOrUpdate(E entity) {
        if (!super.saveOrUpdate(entity)) {
            throw new BusinessException(BusinessMsgState.VERSION_IS_UPDATE);
        }
    }

    /**
     * 根据ID集合删除
     *
     * @param ids ids
     */
    protected void deleteByIds(Collection<Long> ids) {
        if (!super.removeByIds(ids)) {
            throw new BusinessException(BusinessMsgState.OPT_ERROR);
        }
    }

}
