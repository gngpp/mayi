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

package com.zf1976.mayi.upms.biz.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zf1976.mayi.commom.cache.annotation.CacheConfig;
import com.zf1976.mayi.commom.cache.annotation.CacheEvict;
import com.zf1976.mayi.commom.cache.annotation.CachePut;
import com.zf1976.mayi.commom.cache.constants.Namespace;
import com.zf1976.mayi.upms.biz.convert.RoleConvert;
import com.zf1976.mayi.upms.biz.dao.SysDepartmentDao;
import com.zf1976.mayi.upms.biz.dao.SysMenuDao;
import com.zf1976.mayi.upms.biz.dao.SysPermissionDao;
import com.zf1976.mayi.upms.biz.dao.SysRoleDao;
import com.zf1976.mayi.upms.biz.pojo.Permission;
import com.zf1976.mayi.upms.biz.pojo.dto.role.RoleDTO;
import com.zf1976.mayi.upms.biz.pojo.enums.DataPermissionEnum;
import com.zf1976.mayi.upms.biz.pojo.po.SysDepartment;
import com.zf1976.mayi.upms.biz.pojo.po.SysMenu;
import com.zf1976.mayi.upms.biz.pojo.po.SysRole;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.query.RoleQueryParam;
import com.zf1976.mayi.upms.biz.pojo.vo.role.RoleVO;
import com.zf1976.mayi.upms.biz.security.Context;
import com.zf1976.mayi.upms.biz.service.base.AbstractService;
import com.zf1976.mayi.upms.biz.service.exception.RoleException;
import com.zf1976.mayi.upms.biz.service.exception.enums.RoleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色表(SysRole)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:45:58
 */
@Service
@CacheConfig(namespace = Namespace.ROLE, dependsOn = {Namespace.USER, Namespace.PERMISSION})
@Transactional(readOnly = true, rollbackFor = Throwable.class)
public class SysRoleService extends AbstractService<SysRoleDao, SysRole> {

    private final Logger log = LoggerFactory.getLogger("[SysRoleService]");
    private final SysDepartmentDao sysDepartmentDao;
    private final SysMenuDao sysMenuDao;
    private final SysPermissionDao permissionDao;
    private final RoleConvert convert;

    public SysRoleService(SysDepartmentDao sysDepartmentDao, SysPermissionDao permissionDao, SysMenuDao sysMenuDao) {
        this.sysDepartmentDao = sysDepartmentDao;
        this.sysMenuDao = sysMenuDao;
        this.permissionDao = permissionDao;
        this.convert = RoleConvert.INSTANCE;
    }

    /**
     * 所有角色 id，name
     *
     * @return /
     */
    @CachePut(key = "selectAllRole")
    public IPage<RoleVO> findAll() {
        IPage<SysRole> page = super.lambdaQuery()
                                   .select(SysRole::getId, SysRole::getName)
                                   .page(new Page<>(1, 9999));
        return super.mapPageToTarget(page, this.convert::toVo);
    }

    /**
     * 分页查询角色
     *
     * @param query request page
     * @return /
     */
    @CachePut(key = "#query")
    public IPage<RoleVO> findByQuery(Query<RoleQueryParam> query) {
        IPage<SysRole> sourcePage = this.queryWrapper()
                                        .chainQuery(query)
                                        .selectPage();
        return super.mapPageToTarget(sourcePage, sysRole -> {
            sysRole.setDepartmentIds(this.findRoleDepartmentIds(sysRole.getId()));
            sysRole.setMenuIds(this.findRoleMenuIds(sysRole.getId()));
            final var permissions = this.permissionDao.selectPermissionsByRoleId(sysRole.getId())
                                                      .stream()
                                                      .map(Permission::getValue)
                                                      .filter(p -> !StringUtils.isEmpty(p))
                                                      .collect(Collectors.toSet());
            sysRole.setPermissions(permissions);
            return this.convert.toVo(sysRole);
        });
    }

    /**
     * 返回角色级别 0-999 数字越大级别越低
     *
     * @return math
     */
    @CachePut(dynamicsKey = "#username")
    public Integer findByUsernameForLevel(String username) {
        if (Context.isOwner()) {
            return -1;
        }
        return super.baseMapper.selectListByUsername(username)
                               .stream()
                               .map(SysRole::getLevel)
                               .min(Integer::compareTo)
                               .orElse(Integer.MAX_VALUE);
    }

    /**
     * 设置角色状态
     *
     * @param id 角色id
     * @param enabled 状态
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void updateByIdAndEnabled(Long id, Boolean enabled) {
        this.checkBindingRole(id, enabled);
        boolean isUpdate = lambdaUpdate().set(SysRole::getEnabled, enabled)
                                         .eq(SysRole::getId, id)
                                         .update();
        if (!isUpdate) {
            throw new RoleException(RoleState.ROLE_OPT_ERROR);
        }
        return null;
    }

    /**
     * 查询角色
     *
     * @param id id
     * @return role
     */
    public RoleVO findById(Long id) {
        final SysRole sysRole = super.lambdaQuery()
                                     .eq(SysRole::getId, id)
                                     .oneOpt().orElseThrow(() -> new RoleException(RoleState.ROLE_NOT_FOUND));
        sysRole.setDepartmentIds(this.findRoleDepartmentIds(id));
        sysRole.setMenuIds(this.findRoleMenuIds(id));
        return this.convert.toVo(sysRole);
    }

    /**
     * 获取角色所有部门
     *
     * @param id 角色id
     * @return department collection
     */
    private Set<Long> findRoleDepartmentIds(Long id) {
        Assert.notNull(id, "role id cannot be null");
        return this.sysDepartmentDao.selectListByRoleId(id)
                                    .stream()
                                    .filter(SysDepartment::getEnabled)
                                    .map(SysDepartment::getId).collect(Collectors.toSet());
    }

    /**
     * 获取角色所有菜单id
     *
     * @param id 角色id
     * @return id collection
     */
    private Set<Long> findRoleMenuIds(Long id) {
        Assert.notNull(id, "role id cannot be null");
        return this.sysMenuDao.selectListByRoleId(id)
                              .stream()
                              .map(SysMenu::getId)
                              .collect(Collectors.toSet());
    }

    /**
     * 新增角色
     *
     * @param dto role dto
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void saveOne(RoleDTO dto) {
        // 范围消息
        DataPermissionEnum permissionEnum = Optional.ofNullable(dto.getDataScope())
                                                    .orElseThrow(() -> new RoleException(RoleState.ROLE_OPT_ERROR));
        // 校验角色是否已存在
        super.lambdaQuery()
             .eq(SysRole::getName, dto.getName())
             .oneOpt()
             .ifPresent(var -> {
                 throw new RoleException(RoleState.ROLE_EXISTING, var.getName());
             });
        SysRole role = this.convert.toEntity(dto);
        super.savaOrUpdate(role);
        dto.setId(role.getId());
        if (permissionEnum == DataPermissionEnum.ALL) {
            Set<Long> result = this.sysDepartmentDao.selectList(Wrappers.emptyWrapper())
                                                    .stream()
                                                    .map(SysDepartment::getId)
                                                    .collect(Collectors.toSet());
            dto.setDepartmentIds(result);
        }
        this.updateDependent(dto);
        return null;
    }

    /**
     * 更新角色
     *
     * @param dto dto
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void updateOne(RoleDTO dto) {
        this.checkBindingRole(dto.getId(), dto.getEnabled());
        // 范围消息
        DataPermissionEnum permissionEnum = Optional.ofNullable(dto.getDataScope())
                .orElseThrow(() -> new RoleException(RoleState.ROLE_OPT_ERROR));
        // 查询角色是否存在
        SysRole sysRole = super.lambdaQuery()
                .eq(SysRole::getId, dto.getId())
                .oneOpt()
                .orElseThrow(() -> new RoleException(RoleState.ROLE_NOT_FOUND));
        // 校验角色名是否存在
        if (!ObjectUtils.nullSafeEquals(dto.getName(), sysRole.getName())) {
            // 校验角色名是否已存在
            super.lambdaQuery()
                    .eq(SysRole::getName, dto.getName())
                    .oneOpt()
                    .ifPresent(var -> {
                        throw new RoleException(RoleState.ROLE_EXISTING, var.getName());
                    });
        }

        switch (permissionEnum) {
            case ALL -> {
                Set<Long> dataPermission = this.sysDepartmentDao.selectList(Wrappers.emptyWrapper())
                                                                .stream()
                                                                .map(SysDepartment::getId)
                                                                .collect(Collectors.toSet());
                dto.setDepartmentIds(dataPermission);
            }
            case LEVEL -> dto.setDepartmentIds(Collections.emptySet());
            default -> {
            }
        }
        this.convert.copyProperties(dto, sysRole);
        super.savaOrUpdate(sysRole);
        this.updateDependent(dto);
        return null;
    }

    private void checkBindingRole(Long roleId, Boolean bool) {
        if (!bool) {
            // There is a user association that does not allow the current role to be disabled
            if (super.baseMapper.selectUserDependsOnById(roleId) > 0) {
                throw new RoleException(RoleState.ROLE_DEPENDS_ERROR);
            }
        }
    }

    /**
     * 更新 job menu 依赖关系
     * @param dto  dto
     */
    private void updateDependent(RoleDTO dto) {
        Set<Long> idList = Collections.singleton(dto.getId());
        Set<Long> departmentIds = dto.getDepartmentIds();
        if (!CollectionUtils.isEmpty(departmentIds)) {
            super.baseMapper.deleteDepartmentRelationByIds(idList);
            super.baseMapper.saveDepartmentRelationById(dto.getId(), departmentIds);
        }
        Set<Long> menuIds = dto.getMenuIds();
        if (!CollectionUtils.isEmpty(menuIds)) {
            super.baseMapper.deleteMenuRelationByIds(idList);
            super.baseMapper.saveMenuRelationById(dto.getId(), menuIds);
        }
    }

    /**
     * 删除角色
     *
     * @param ids id集合
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void deleteByIds(Set<Long> ids) {
        ids.forEach(id -> {
            if (super.baseMapper.selectUserDependsOnById(id) > 0) {
                throw new RoleException(RoleState.ROLE_DEPENDS_ERROR);
            }
        });
        // 删除role
        super.deleteByIds(ids);
        // 删除role-menu
        super.baseMapper.deleteMenuRelationByIds(ids);
        // 删除role-department
        super.baseMapper.deleteDepartmentRelationByIds(ids);
        // 删除user-role
        super.baseMapper.deleteUserRelationByIds(ids);
        // 删除role-permission
        super.baseMapper.deletePermissionRelationByIds(ids);
        return null;
    }

}
