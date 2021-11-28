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
import com.zf1976.mayi.upms.biz.pojo.po.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


/**
 * 系统菜单(SysMenu)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-31 11:44:06
 */
@Repository
public interface SysMenuDao extends BaseMapper<SysMenu> {

    /**
     * 查询角色所有菜单
     *
     * @param roleId id
     * @return 菜单列表
     */
    List<SysMenu> selectListByRoleId(@Param("roleId") long roleId);

    /**
     * 根据角色id集合查询所有菜单
     *
     * @param roleIds ids
     * @return 菜单列表
     */
    List<SysMenu> selectListByRoleIds(@Param("roleIds") Collection<Long> roleIds);

    /**
     * 删除角色相关菜单
     *
     * @param ids ids
     */
    void deleteRoleRelationByIds(@Param("ids") Collection<Long> ids);

    /**
     * 查询菜单跟角色依赖关系
     *
     * @param id id
     * @return count
     */
    Long selectRoleDependsOnById(@Param("id") Long id);

    /**
     * 查询菜单最大类型值
     *
     * @return max type
     */
    Long selectMaxType();

}
