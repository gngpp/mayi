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

package com.zf1976.mayi.upms.biz.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zf1976.mayi.common.component.cache.annotation.CacheConfig;
import com.zf1976.mayi.common.component.cache.annotation.CacheEvict;
import com.zf1976.mayi.common.component.cache.annotation.CachePut;
import com.zf1976.mayi.common.component.cache.constants.Namespace;
import com.zf1976.mayi.upms.biz.dao.SysPermissionDao;
import com.zf1976.mayi.upms.biz.pojo.dto.PermissionDTO;
import com.zf1976.mayi.upms.biz.pojo.po.SysPermission;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.vo.PermissionVO;
import com.zf1976.mayi.upms.biz.security.convert.SecurityConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Set;

/**
 * @author mac
 * @date 2021/5/11
 */
@Service
@CacheConfig(
        namespace = Namespace.PERMISSION,
        dependsOn = {Namespace.ROLE, Namespace.RESOURCE},
        postInvoke = {"initialize"}
)
@Transactional(readOnly = true, rollbackFor = Throwable.class)
public class PermissionService extends AbstractSecurityService<SysPermissionDao, SysPermission> implements InitPermission{

    private final SecurityConvert convert = SecurityConvert.INSTANCE;
    private DynamicDataSourceService dynamicDataSourceService;

    @Autowired
    public void setDynamicDataSourceService(DynamicDataSourceService dynamicDataSourceService) {
        this.dynamicDataSourceService = dynamicDataSourceService;
    }

    /**
     * 根据分页对象分页查询权限列表
     *
     * @param query 查询对象
     * @return {@link IPage<PermissionVO>}
     */
    @CachePut(key = "#query")
    public IPage<PermissionVO> selectPermissionByPage(Query<?> query) {
        Page<SysPermission> permissionPage = super.lambdaQuery().page(query.toPage());
        return super.mapToTarget(permissionPage, convert::toPermissionVO);
    }

    /**
     * 新增权限数据
     *
     * @date 2021-05-12 09:37:12
     * @param permissionDTO DTO
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void savePermission(PermissionDTO permissionDTO) {
        // 判断权限值是否已经存在
        if (this.checkExitsPermissionValue(permissionDTO.getValue())) {
            throw new SecurityException("permission value：" + permissionDTO.getValue() + "is exist");
        }
        SysPermission sysPermission = this.convert.toPermissionEntity(permissionDTO);
        if (!super.saveOrUpdate(sysPermission)) {
            throw new SecurityException("Permission data insert failed");
        }
        return null;
    }

    /**
     * 更新权限数据
     *
     * @date 2021-05-12 09:36:52
     * @param permissionDTO DTO
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void updatePermission(PermissionDTO permissionDTO) {
        // 判断权限该权限实体是否存在
        SysPermission sysPermission = super.lambdaQuery()
                                           .eq(SysPermission::getId, permissionDTO.getId())
                                           .oneOpt()
                                           .orElseThrow(() -> new SecurityException("permission does not exist"));
        // 如果更新的权限值改变
        if (!ObjectUtils.nullSafeEquals(sysPermission.getValue(), permissionDTO.getValue())) {
            // 判断权限值是否已经存在
            if (this.checkExitsPermissionValue(permissionDTO.getValue())) {
                throw new SecurityException("permission value：" + permissionDTO.getValue() + "is exist");
            }
        }
        this.convert.copyProperties(permissionDTO, sysPermission);
        if (!super.saveOrUpdate(sysPermission)) {
            throw new SecurityException("Permission data update failed");
        }
        return null;
    }

    /**
     * 检查权限值是否已存在
     *
     * @date 2021-05-12 09:36:16
     * @param value 泉限值
     * @return {@link boolean}
     */
    private boolean checkExitsPermissionValue(String value) {
        return super.lambdaQuery()
                    .eq(SysPermission::getValue, value)
                    .oneOpt()
                    .isPresent();
    }

    /**
     * 根据权限属性id删除
     *
     * @date 2021-05-12 00:46:53
     * @param id 权限id
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void deletePermissionById(Long id) {
        if (id == null) {
            throw new SecurityException("permission id cannot been null");
        }
        super.removeById(id);
        // 删除权限-角色关系
        super.baseMapper.deleteRoleRelationById(id);
        // 删除权限-资源关系
        super.baseMapper.deleteResourceRelationById(id);
        return null;
    }

    /**
     * 根据id集合删除
     *
     * @date 2021-05-12 20:26:06
     * @param ids id集合
     * @return {@link Void}
     */
    @CacheEvict
    @Transactional
    public Void deletePermissionByIds(Set<Long> ids) {
        if (!super.removeByIds(ids)) {
            throw new SecurityException("permission ids is empty");
        }
        for (Long id : ids) {
            // 删除权限-角色关系
            super.baseMapper.deleteRoleRelationById(id);
            // 删除权限-资源关系
            super.baseMapper.deleteResourceRelationById(id);
        }
        return null;
    }

    @Override
    public void initialize() {
        this.dynamicDataSourceService.initialize();
    }
}
