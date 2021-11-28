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
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.upms.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zf1976.mayi.upms.biz.pojo.Permission;
import com.zf1976.mayi.upms.biz.pojo.RoleBinding;
import com.zf1976.mayi.upms.biz.pojo.po.SysPermission;
import com.zf1976.mayi.upms.biz.pojo.po.SysResource;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author mac
 * @date 2020/12/25
 **/
@Repository
public interface SysPermissionDao extends BaseMapper<SysPermission> {

    /**
     * 获取资源权限值
     *
     * @param resourceId 资源id
     * @return {@link List<String>}
     */
    List<Permission> selectPermissionsByResourceId(@Param("resourceId") long resourceId);

    /**
     * 获取角色权限值
     *
     * @param roleId 角色id
     * @return {@link List<String>}
     */
    List<Permission> selectPermissionsByRoleId(@Param("roleId") long roleId);

    /**
     * 查询资源权限绑定
     *
     * @return {@link List<SysResource>}
     */
    List<SysResource> selectResourceBindingList();

    /**
     * 查询绑定权限的角色列表
     *
     * @return {@link List<RoleBinding>}
     */
    List<RoleBinding> selectRoleBindingList();

    /**
     * 保存权限与资源关系
     *
     * @param resourceId 资源id
     * @param permissionIdList 权限id列表
     */
    void saveResourceRelation(@Param("resourceId") long resourceId, @Param("permissionIdList") Collection<Long> permissionIdList);

    /**
     * 保存权限与校色关系
     *
     * @param roleId 角色id
     * @param permissionIdList 权限id列表
     */
    void saveRoleRelation(@Param("roleId") long roleId, @Param("permissionIdList") Collection<Long> permissionIdList);

    /**
     * 根据权限id删除 权限-资源关系
     *
     * @param id 权限id
     */
    void deleteResourceRelationById(@Param("id") long id);

    /**
     * 根据权限id删除 权限-角色关系
     *
     * @param id 权限id
     */
    void deleteRoleRelationById(@Param("id") long id);

    /**
     * 根据资源id，权限id集合进行解绑
     *
     * @param resourceId 资源id
     * @param permissionIdList 权限id集合
     */
    void deleteResourceRelationByResourceIdAndPermissionIdList(@Param("resourceId") long resourceId, @Param("permissionIdList") Collection<Long> permissionIdList);

    /**
     * 根据角色id，权限id集合进行解绑
     *
     * @param roleId 角色id
     * @param permissionIdList 权限id集合
     */
    void deleteRoleRelationByRoleIdAndPermissionIdList(@Param("roleId") long roleId, @Param("permissionIdList") Collection<Long> permissionIdList);
}
