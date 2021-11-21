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

package com.zf1976.mayi.upms.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zf1976.mayi.upms.biz.pojo.po.SysDepartment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


/**
 * 部门(SysDept)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-31 11:44:03
 */
@Repository
public interface SysDepartmentDao extends BaseMapper<SysDepartment> {

    /**
     * 获取角色部门
     *
     * @param roleId 角色id
     * @return dept set
     */
    List<SysDepartment> selectListByRoleId(@Param("roleId") long roleId);

    /**
     * 获取子部门
     *
     * @param id 部门id
     * @return 部门列表
     */
    List<SysDepartment> selectChildrenById(@Param("id")long id);

    /**
     * 删除角色相关部门
     *
     * @param ids ids
     */
    void deleteRoleRelationByIds(@Param("ids") Collection<Long> ids);

    /**
     * 查询部门跟角色依赖关系
     *
     * @param id id
     * @return /
     */
    Long selectDependsOnById(@Param("id") long id);

    /**
     * 查询依赖关系role id 集合
     *
     * @param id department id
     * @return ids
     */
    List<Long> selectRoleRelationById(@Param("id") long id);

    /**
     * 根据id集合查询角色依赖
     *
     * @param ids ids
     * @return 角色id集合
     */
    List<Long> selectRoleRelationByIds(@Param("ids") Collection<Long> ids);

}
