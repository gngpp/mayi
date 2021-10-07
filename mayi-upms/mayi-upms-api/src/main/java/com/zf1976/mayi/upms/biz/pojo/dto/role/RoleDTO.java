package com.zf1976.mayi.upms.biz.pojo.dto.role;

import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import com.zf1976.mayi.upms.biz.pojo.enums.DataPermissionEnum;

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
