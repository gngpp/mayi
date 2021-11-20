package com.zf1976.mayi.upms.biz.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zf1976.mayi.common.component.cache.annotation.CacheConfig;
import com.zf1976.mayi.common.component.cache.annotation.CacheEvict;
import com.zf1976.mayi.common.component.cache.annotation.CachePut;
import com.zf1976.mayi.common.core.constants.Namespace;
import com.zf1976.mayi.upms.biz.convert.SysRoleConvert;
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
@Transactional(rollbackFor = Throwable.class)
public class SysRoleService extends AbstractService<SysRoleDao, SysRole> {

    private final Logger log = LoggerFactory.getLogger("[SysRoleService]");
    private final SysDepartmentDao sysDepartmentDao;
    private final SysMenuDao sysMenuDao;
    private final SysPermissionDao permissionDao;
    private final SysRoleConvert convert;

    public SysRoleService(SysDepartmentDao sysDepartmentDao, SysPermissionDao permissionDao, SysMenuDao sysMenuDao) {
        this.sysDepartmentDao = sysDepartmentDao;
        this.sysMenuDao = sysMenuDao;
        this.permissionDao = permissionDao;
        this.convert = SysRoleConvert.INSTANCE;
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
    @Transactional(readOnly = true)
    public IPage<RoleVO> findByQuery(Query<RoleQueryParam> query) {
        IPage<SysRole> sourcePage = this.queryWrapper()
                                        .chainQuery(query)
                                        .selectPage();
        return super.mapPageToTarget(sourcePage, sysRole -> {
            sysRole.setDepartmentIds(this.selectRoleDepartmentIds(sysRole.getId()));
            sysRole.setMenuIds(this.selectRoleMenuIds(sysRole.getId()));
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
     * @return /
     */
    @CacheEvict
    @Transactional
    public Void updateByIdAndEnabled(Long id, Boolean enabled) {
        if (!enabled) {
            // 存在用户关联不允许禁用当前角色
            if (super.baseMapper.selectUserDependsOnById(id) > 0) {
                throw new RoleException(RoleState.ROLE_DEPENDS_ERROR);
            }
        }
        boolean isUpdate = lambdaUpdate().set(SysRole::getEnabled, enabled)
                                         .eq(SysRole::getId, id)
                                         .update();
        if (!isUpdate) {
            throw new RoleException(RoleState.ROLE_NOT_FOUND);
        }
        return null;
    }

    /**
     * 查询角色
     *
     * @param id id
     * @return role
     */
    @Transactional(readOnly = true)
    public RoleVO findById(Long id) {
        final SysRole sysRole = super.lambdaQuery()
                                     .eq(SysRole::getId, id)
                                     .oneOpt().orElseThrow(() -> new RoleException(RoleState.ROLE_NOT_FOUND));
        sysRole.setDepartmentIds(this.selectRoleDepartmentIds(id));
        sysRole.setMenuIds(this.selectRoleMenuIds(id));
        return this.convert.toVo(sysRole);
    }

    /**
     * 获取角色所有部门
     *
     * @param id 角色id
     * @return department collection
     */
    private Set<Long> selectRoleDepartmentIds(Long id) {
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
    private Set<Long> selectRoleMenuIds(Long id) {
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
     * @return /
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
     * @return /
     */
    @CacheEvict
    @Transactional
    public Void updateOne(RoleDTO dto) {
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
            case ALL:
                Set<Long> dataPermission = this.sysDepartmentDao.selectList(Wrappers.emptyWrapper())
                        .stream()
                        .map(SysDepartment::getId)
                        .collect(Collectors.toSet());
                dto.setDepartmentIds(dataPermission);
                break;
            case LEVEL:
                dto.setDepartmentIds(Collections.emptySet());
                break;
            default:
                break;
        }
        this.convert.copyProperties(dto, sysRole);
        super.savaOrUpdate(sysRole);
        this.updateDependent(dto);
        return null;
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
     * @return /
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
