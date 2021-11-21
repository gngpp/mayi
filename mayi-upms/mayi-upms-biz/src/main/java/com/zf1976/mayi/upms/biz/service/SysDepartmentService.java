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

import cn.hutool.core.collection.ConcurrentHashSet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.common.component.cache.annotation.CacheConfig;
import com.zf1976.mayi.common.component.cache.annotation.CacheEvict;
import com.zf1976.mayi.common.component.cache.annotation.CachePut;
import com.zf1976.mayi.common.core.constants.Namespace;
import com.zf1976.mayi.common.core.foundation.exception.BusinessException;
import com.zf1976.mayi.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.mayi.upms.biz.convert.DepartmentConvert;
import com.zf1976.mayi.upms.biz.dao.SysDepartmentDao;
import com.zf1976.mayi.upms.biz.pojo.dto.dept.DepartmentDTO;
import com.zf1976.mayi.upms.biz.pojo.po.SysDepartment;
import com.zf1976.mayi.upms.biz.pojo.query.DeptQueryParam;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.vo.dept.DepartmentExcelVO;
import com.zf1976.mayi.upms.biz.pojo.vo.dept.DepartmentVO;
import com.zf1976.mayi.upms.biz.service.base.AbstractService;
import com.zf1976.mayi.upms.biz.service.exception.DepartmentException;
import com.zf1976.mayi.upms.biz.service.exception.enums.DepartmentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 部门(SysDept)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:45:57
 */
@Service
@CacheConfig(namespace = Namespace.DEPARTMENT, dependsOn = {Namespace.ROLE, Namespace.USER})
@Transactional(rollbackFor = Throwable.class)
public class SysDepartmentService extends AbstractService<SysDepartmentDao, SysDepartment> {

    private static final int MAX_PAGE_DEPARTMENT = 9999;
    private final Logger log = LoggerFactory.getLogger("[SysDepartmentService]");
    private final DepartmentConvert convert = DepartmentConvert.INSTANCE;

    /**
     * 按条件分页查询部门
     *
     * @param page page param
     * @return dept list page
     */
    @CachePut(key = "#page")
    public IPage<DepartmentVO> findByQuery(Query<DeptQueryParam> page) {
        IPage<SysDepartment> sourcePage = super.queryWrapper()
                                               .chainQuery(page)
                                               .selectPage();
        return this.departmentTreeBuilder(sourcePage);
    }

    /**
     * 排除本级部门所在部门树
     *
     * @param id dept id
     * @return 满足前提条件的部门树
     */
    @CachePut(key = "#id")
    @Transactional(readOnly = true)
    public IPage<DepartmentVO> findVertexById(Long id) {
        // 查询部门是否存在
        super.lambdaQuery()
             .eq(SysDepartment::getId, id)
             .oneOpt()
             .orElseThrow(() -> new BusinessException(BusinessMsgState.DATA_NOT_FOUNT));
        // 获取查询页
        IPage<SysDepartment> sourcePage = super.queryWrapper()
                                               .chainQuery(new Query<>(1, MAX_PAGE_DEPARTMENT))
                                               .selectPage();
        // 收集下级部门id集合
        Set<Long> nextLowDeptIds = this.collectCurrentChildrenDepartmentIds(id, null, new ConcurrentHashSet<>());
        // 过滤掉下级部门 以及本部门
        final List<SysDepartment> collect = sourcePage.getRecords()
                                                      .stream()
                                                      .filter(sysDept -> !nextLowDeptIds.contains(sysDept.getId()))
                                                      .sorted(Comparator.comparingInt(SysDepartment::getDeptSort))
                                                      .collect(Collectors.toList());
        // 构建部门树并返回
        return this.departmentTreeBuilder(sourcePage.setRecords(collect));
    }

    /**
     * 收集顶点
     *
     * @param sysDepartment 部门
     * @param deptList      部门列表
     * @return /
     */
    private List<SysDepartment> collectDepartmentVertex(SysDepartment sysDepartment, List<SysDepartment> deptList) {
        if (ObjectUtils.isEmpty(sysDepartment) || ObjectUtils.isEmpty(sysDepartment.getPid())) {
            List<SysDepartment> var3 = super.lambdaQuery()
                                            .isNull(SysDepartment::getPid)
                                            .list();
            // 顶级部门
            deptList.addAll(var3);
            return deptList;
        }
        // 查询同级部门 同时包含本部门
        List<SysDepartment> var1 = super.lambdaQuery()
                                        .eq(SysDepartment::getPid, sysDepartment.getPid())
                                        .list();
        deptList.addAll(var1);
        // 获取上级部门
        SysDepartment var2 = super.lambdaQuery()
                                  .eq(SysDepartment::getId, sysDepartment.getPid())
                                  .one();
        return collectDepartmentVertex(var2, deptList);
    }

    /**
     * 构建部门树
     *
     * @param sourcePage source page
     */
    private IPage<DepartmentVO> departmentTreeBuilder(IPage<SysDepartment> sourcePage) {
        final IPage<DepartmentVO> targetPage = super.mapPageToTarget(sourcePage, this.convert::toVo);
        // 所有节点
        final List<DepartmentVO> list = targetPage.getRecords();
        // 构建tree
        for (DepartmentVO parent : list) {
            for (DepartmentVO child : list) {
                // 当前节点是上级节点的子节点
                if (ObjectUtils.nullSafeEquals(parent.getId(), child.getPid())) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(child);
                }
            }
            // 设置dept node properties
            this.setDepartmentNodeProperties(parent);
        }
        // 清除已被添加的节点
        final List<DepartmentVO> target = list.stream().filter(departmentVO -> departmentVO.getPid() == null)
                                                .sorted(Comparator.comparingInt(DepartmentVO::getDeptSort))
                                                .collect(Collectors.toList());
        return targetPage.setRecords(Collections.unmodifiableList(target));
    }

    /**
     * 设置 tree properties
     *
     * @param vo vo
     */
    private void setDepartmentNodeProperties(DepartmentVO vo) {
        final boolean empty = CollectionUtils.isEmpty(vo.getChildren());
        vo.setHasChildren(!empty);
        vo.setLeaf(empty);
    }

    /**
     * 新增部门
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional
    public Void savaOne(DepartmentDTO dto) {
        // 确认部门是否存在
        super.lambdaQuery()
             .eq(SysDepartment::getName, dto.getName())
             .oneOpt()
             .ifPresent(sysDept -> {
                 throw new DepartmentException(DepartmentState.DEPARTMENT_EXISTING, dto.getName());
             });
        SysDepartment department = this.convert.toEntity(dto);
        super.savaOrUpdate(department);
        return null;
    }

    /**
     * 更新部门
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional
    public Void updateOne(DepartmentDTO dto) {
        // 查询部门
        SysDepartment department = super.lambdaQuery()
                                           .eq(SysDepartment::getId, dto.getId())
                                           .oneOpt()
                                           .orElseThrow(() -> new DepartmentException(DepartmentState.DEPARTMENT_NOT_FOUND));
        // 确认部门是否已存在
        if (!ObjectUtils.nullSafeEquals(dto.getName(), department.getName())) {
            super.lambdaQuery()
                 .ne(SysDepartment::getId, department.getId())
                 .eq(SysDepartment::getName, dto.getName())
                 .oneOpt()
                 .ifPresent(var1 -> {
                     throw new DepartmentException(DepartmentState.DEPARTMENT_EXISTING, dto.getName());
                 });
        }
        // 父部门及以下所有部门关闭
        if (!ObjectUtils.isEmpty(dto.getEnabled()) && !dto.getEnabled()) {
            // 关闭子部门
            super.lambdaQuery()
                 .eq(SysDepartment::getPid, dto.getId())
                 .list()
                 .forEach(dept -> {
                     dept.setEnabled(dto.getEnabled());
                     super.savaOrUpdate(dept);
                     this.closeChildrenDepartment(dept.getId(), dto.getEnabled());
                 });
        }
        // 禁止状态下禁止修改
        if (dto.getPid() != null) {
            final SysDepartment parentDept = super.lambdaQuery()
                                                  .select(SysDepartment::getEnabled)
                                                  .eq(SysDepartment::getId, dto.getPid())
                                                  .oneOpt().orElseThrow(() -> new DepartmentException(DepartmentState.DEPARTMENT_NOT_FOUND));
            if (!parentDept.getEnabled()) {
                throw new DepartmentException(DepartmentState.DEPARTMENT_PARENT_CLOSE);
            }
        }
        // 所有子部门id集合
        final Set<Long> childrenDeptIds = this.collectCurrentChildrenDepartmentIds(department.getId(), null, new ConcurrentHashSet<>());
        // 禁止以子部门为父级部门
        if (!childrenDeptIds.isEmpty() && dto.getPid() != null) {
            if (childrenDeptIds.contains(dto.getPid())) {
                throw new DepartmentException(DepartmentState.DEPARTMENT_BAN_PARENT);
            }
        }
        // 禁止以本级部门为父级部门
        if (ObjectUtils.nullSafeEquals(dto.getPid(), department.getId())) {
            throw new DepartmentException(DepartmentState.DEPARTMENT_BAN_CURRENT);
        }
        // 复制属性
        this.convert.copyProperties(dto, department);
        // 更新实体
        super.savaOrUpdate(department);
        return null;
    }


    /**
     * 删除 部门包含所有子部门
     *
     * @param ids ids
     * @return /
     */
    @CacheEvict
    @Transactional
    public Void deleteByIds(Set<Long> ids) {
        final Set<Long> treeIds = this.collectCurrentDepartmentTreeIds(ids, new HashSet<>());
        if (!CollectionUtils.isEmpty(treeIds)) {
            treeIds.forEach(id -> {
                if (super.baseMapper.selectDependsOnById(id) > 0) {
                    throw new DepartmentException(DepartmentState.DEPARTMENT_DEPENDS_ERROR);
                }
            });
        }
        // 删除department
        super.deleteByIds(treeIds);
        // 删除 role-department
        super.baseMapper.deleteRoleRelationByIds(ids);
        return null;
    }

    /**
     * 收集本级部门以及所有子部门id集合
     *
     * @param ids id collection
     */
    private Set<Long> collectCurrentDepartmentTreeIds(Set<Long> ids, Set<Long> supplier) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptySet();
        }
        Assert.notNull(supplier, "collectionIds can not been null");
        supplier.addAll(ids);
        ids.forEach(id -> {
            // 子部门id集合
            final Set<Long> collectIds = this.collectNextLowDepartmentIds(id, null);
            // 收集子部门id集合
            this.collectCurrentDepartmentTreeIds(collectIds, supplier);
        });
        return supplier;
    }

    /**
     * 收集本级部门下所有子部门id集合
     *
     * @param id        current dept id
     * @param condition status
     * @param supplier  supplied object
     * @return ids
     */
    private Set<Long> collectCurrentChildrenDepartmentIds(long id, Boolean condition, Set<Long> supplier) {
        // 子部门id集合
        final Set<Long> childrenIds = this.collectNextLowDepartmentIds(id, condition);
        // 收集
        supplier.addAll(childrenIds);
        if (!CollectionUtils.isEmpty(childrenIds)) {
            // 继续往下子部门收集
            for (Long childrenId : childrenIds) {
                this.collectCurrentChildrenDepartmentIds(childrenId, condition, supplier);
            }
        }
        return supplier;
    }

    /**
     * 收集下级子部门id集合
     *
     * @param id        id
     * @param condition status
     * @return children ids
     */
    private Set<Long> collectNextLowDepartmentIds(Long id, Boolean condition) {
        // 子部门id集合
        return super.lambdaQuery()
                    .eq(condition != null, SysDepartment::getEnabled, condition)
                    .eq(SysDepartment::getPid, id)
                    .list()
                    .stream()
                    .map(SysDepartment::getId)
                    .collect(Collectors.toSet());
    }

    /**
     * 设置所有子部门关闭
     *
     * @param id         部门id
     * @param closeValue value
     */
    private void closeChildrenDepartment(Long id, boolean closeValue) {
        //获取子部门
        super.lambdaQuery()
             .eq(SysDepartment::getPid, id)
             .list()
             .forEach(var2 -> {
                 var2.setEnabled(closeValue);
                 super.savaOrUpdate(var2);
                 this.closeChildrenDepartment(var2.getId(), closeValue);
             });
    }

    /**
     * 是否存在子部门
     *
     * @param id parent id
     * @return boolean
     */
    private boolean hasChildrenDepartment(long id) {
        final Integer count = super.lambdaQuery()
                                   .eq(SysDepartment::getPid, id)
                                   .count();
        return count > 0;
    }

    /**
     * 确认下一级部门 开启状态
     *
     * @param id id
     * @return boolean
     */
    private boolean selectChildDepartmentStatus(long id, boolean status) {
        final long count = super.lambdaQuery()
                                .eq(SysDepartment::getPid, id)
                                .eq(SysDepartment::getEnabled, status)
                                .count();
        return count > 0;
    }

    /**
     * 下载部门信息
     *
     * @param query request
     * @param response    response
     */
    @Transactional(readOnly = true)
    public Void downloadExcel(Query<DeptQueryParam> query, HttpServletResponse response) {
        List<SysDepartment> departmentList = super.queryWrapper()
                                                  .chainQuery(query)
                                                  .selectList();
        final List<DepartmentVO> deptTree = super.mapListToTarget(departmentList, this.convert::toVo);
        final Collection<Map<String, Object>> mapCollection = this.downloadDeptTreeBuilder(new LinkedList<>(), deptTree);
        super.downloadExcel(mapCollection, response);
        return null;
    }

    private Collection<Map<String, Object>> downloadDeptTreeBuilder(Collection<Map<String,Object>> mapCollection, Collection<DepartmentVO> deptTree) {
        deptTree.forEach(var0 -> {
            Map<String,Object> map = new HashMap<>(16);
            final DepartmentExcelVO downloadVo = new DepartmentExcelVO();
            downloadVo.setName(var0.getName());
            downloadVo.setPid(var0.getPid());
            downloadVo.setEnabled(var0.getEnabled());
            downloadVo.setCreateTime(var0.getCreateTime());
            if (!CollectionUtils.isEmpty(var0.getChildren())) {
                downloadVo.setChildren(this.downloadDeptTreeBuilder(new LinkedList<>(), var0.getChildren()));
            }
            super.setProperties(map, downloadVo);
            mapCollection.add(map);
        });
        return mapCollection;
    }
}
