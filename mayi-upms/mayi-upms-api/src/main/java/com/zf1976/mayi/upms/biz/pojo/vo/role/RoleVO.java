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

package com.zf1976.mayi.upms.biz.pojo.vo.role;

import com.zf1976.mayi.upms.biz.pojo.enums.DataPermissionEnum;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author mac
 * @date 2020/11/21
 **/
public class RoleVO implements Serializable {

    private static final long serialVersionUID = -1044981781904947290L;

    /**
     * id
     */
    private Long id;

    /**
     * 角色所有部门
     */
    private List<Long> departmentIds;

    /**
     * 角色权限
     */
    private Set<String> permissions;

    /**
     * 角色所有菜单id
     */
    private Set<Long> menuIds;

    /**
     * 名称
     */
    private String name;

    /**
     * 角色级别
     */
    private Integer level;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     */
    private Boolean enabled;

    /**
     * 数据权限
     */
    private DataPermissionEnum dataScope;

    /**
     * 创建日期
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public RoleVO setPermissions(Set<String> permissions) {
        this.permissions = permissions;
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getDepartmentIds() {
        return departmentIds;
    }

    public void setDepartmentIds(List<Long> departmentIds) {
        this.departmentIds = departmentIds;
    }

    public Set<Long> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(Set<Long> menuIds) {
        this.menuIds = menuIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public DataPermissionEnum getDataScope() {
        return dataScope;
    }

    public void setDataScope(DataPermissionEnum dataScope) {
        this.dataScope = dataScope;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "RoleVO{" +
                "id=" + id +
                ", departmentIds=" + departmentIds +
                ", permissions=" + permissions +
                ", menuIds=" + menuIds +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                ", dataScope=" + dataScope +
                ", createTime=" + createTime +
                '}';
    }
}
