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

package com.gngpp.mayi.upms.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gngpp.mayi.upms.biz.pojo.po.SysRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


/**
 * 角色表(SysRole)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-31 11:44:04
 */
@Repository
public interface SysRoleDao extends BaseMapper<SysRole> {

    /**
     * 获取用户角色
     *
     * @param userId 用户ID
     * @return roles
     */
    List<SysRole> selectListByUserId(@Param("userId") long userId);

    /**
     * 获取用户角色
     *
     * @param username 用户名
     * @return roles
     */
    List<SysRole> selectListByUsername(@Param("username") String username);

    /**
     * 删除角色相关菜单
     *
     * @param ids ids
     */
    void deleteMenuRelationByIds(@Param("ids") Collection<Long> ids);

    /**
     * 删除角色相关部门
     *
     * @param ids ids
     */
    void deleteDepartmentRelationByIds(@Param("ids") Collection<Long> ids);

    /**
     * 删除用户依赖关系
     *
     * @param ids ids
     */
    void deleteUserRelationByIds(@Param("ids") Collection<Long> ids);

    /**
     * 删除权限依赖关系
     *
     * @param ids 角色id集合
     */
    void deletePermissionRelationByIds(@Param("ids") Collection<Long> ids);

    /**
     * 查询角色依赖关系
     *
     * @param id id
     * @return /
     */
    Long selectUserDependsOnById(@Param("id") long id);

    /**
     * 添加角色与部门依赖关系
     *
     * @param id            id
     * @param departmentIds 部门id
     */
    void saveDepartmentRelationById(@Param("id") long id, @Param("departmentIds") Collection<Long> departmentIds);

    /**
     * 添加角色与菜单依赖关系
     *
     * @param id      id
     * @param menuIds menu id collection
     */
    void saveMenuRelationById(@Param("id") long id, @Param("menuIds") Collection<Long> menuIds);

}
