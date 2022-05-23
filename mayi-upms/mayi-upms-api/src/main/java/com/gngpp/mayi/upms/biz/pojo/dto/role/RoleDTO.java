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

package com.gngpp.mayi.upms.biz.pojo.dto.role;

import com.gngpp.mayi.common.core.validate.ValidationInsertGroup;
import com.gngpp.mayi.common.core.validate.ValidationUpdateGroup;
import com.gngpp.mayi.upms.biz.pojo.enums.DataPermissionEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Set;

/**
 * @author mac
 * @date 2020/11/21
 **/
public class RoleDTO {

    /**
     * id
     */
    @Null(groups = ValidationInsertGroup.class)
    @NotNull(groups = ValidationUpdateGroup.class)
    private Long id;

    /**
     * role name
     */
    @NotBlank
    private String name;

    /**
     * role level
     */
    @NotNull
    private Integer level;

    /**
     * 状态
     */
    @NotNull
    private Boolean enabled;

    /**
     * data scope description
     */
    @NotNull
    private DataPermissionEnum dataScope;

    /**
     * description
     */
    @NotBlank
    private String description;

    /**
     * department id collection
     */
    @NotNull(groups = ValidationUpdateGroup.class, message = "部门不能为NULL")
    private Set<Long> departmentIds;

    /**
     * menu id collection
     */
    @NotNull(groups = ValidationUpdateGroup.class, message = "菜单不能为NULL")
    private Set<Long> menuIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getDepartmentIds() {
        return departmentIds;
    }

    public void setDepartmentIds(Set<Long> departmentIds) {
        this.departmentIds = departmentIds;
    }

    public Set<Long> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(Set<Long> menuIds) {
        this.menuIds = menuIds;
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", enabled=" + enabled +
                ", dataScope=" + dataScope +
                ", description='" + description + '\'' +
                ", departmentIds=" + departmentIds +
                ", menuIds=" + menuIds +
                '}';
    }
}
