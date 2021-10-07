package com.zf1976.mayi.upms.biz.pojo.vo.role;

import com.zf1976.mayi.upms.biz.pojo.enums.DataPermissionEnum;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author mac
 * @date 2020/11/21
 **/
public class RoleVO {

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
