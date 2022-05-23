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
import com.gngpp.mayi.upms.biz.pojo.po.SysUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


/**
 * 系统用户(SysUser)表数据库访问层
 *
 * @author makejava
 * @since 2020-08-31 11:44:06
 */
@Repository
public interface SysUserDao extends BaseMapper<SysUser> {

    /**
     * 查询用户信息
     *
     * @param username 用户名
     * @return result
     */
    @Deprecated
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "departmentId", column = "department_id"),
            @Result(property = "department", column = "department_id",
                    one = @One(select = "com.gngpp.mayi.upms.biz.dao.SysDepartmentDao.selectById")),
            @Result(property = "roleList", column = "id",
                    many = @Many(select = "com.gngpp.mayi.upms.biz.dao.SysRoleDao.selectListByUserId")),
            @Result(property = "positionList", column = "id",
                    many = @Many(select = "com.gngpp.mayi.upms.biz.dao.SysPositionDao.selectListByUserId"))
    })
    @Select(value = "select * from sys_user where username = #{username}")
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据用户名查询用户，包含部门，用户角色，用户职位
     *
     * @param username 用户名
     * @return {@link SysUser}
     */
    SysUser selectOneByUsername(@Param("username") String username);

    /**
     * 根据用户ID，查询用户角色依赖的ID集合
     *
     * @param id 用户id
     * @return {@link List<Long>}
     */
    List<Long> selectRoleRelationById(@Param("id") Long id);

    /**
     * 根据用户ID，查询用户岗位依赖的ID集合
     *
     * @param id 用户id
     * @return {@link List<Long>}
     */
    List<Long> selectPositionRelationById(@Param("id") Long id);

    /**
     * 添加岗位依赖关系
     *
     * @param id     id
     * @param jobIds job collection id
     */
    void savePositionRelationById(@Param("id") long id, @Param("jobIds") Collection<Long> jobIds);

    /**
     * 添加角色依赖关系
     *
     * @param id id
     * @param roleIds role collection id
     */
    void savaRoleRelationById(@Param("id") long id, @Param("roleIds") Collection<Long> roleIds);

    /**
     * 删除角色依赖关系
     *
     * @param id id
     */
    void deleteRoleRelationById(@Param("id") Long id);

    /**
     * 删除岗位依赖关系
     *
     * @param id id
     */
    void deletePositionRelationById(@Param("id") Long id);

    /**
     * 根据角色id集合查询关联用户id集合
     *
     * @param roleIds ids
     * @return user ids
     */
    List<Long> selectIdsByRoleIds(@Param("roleIds") Collection<Long> roleIds);

    /**
     * 根据部门集合id查询关联用户id集合
     *
     * @param departmentIds ids
     * @return user ids
     */
    List<Long> selectIdsByDepartmentIds(@Param("departmentIds") Collection<Long> departmentIds);
}
