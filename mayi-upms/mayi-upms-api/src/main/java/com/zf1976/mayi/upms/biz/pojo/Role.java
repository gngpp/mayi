package com.zf1976.mayi.upms.biz.pojo;

import com.zf1976.mayi.upms.biz.pojo.enums.DataPermissionEnum;

import java.io.Serializable;

/**
 * @author ant
 * Create by Ant on 2020/9/8 9:02 下午
 */
public class Role implements Serializable {

    /**
     * 角色id
     */
    private Long id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 状态
     */
    private Boolean enabled;

    /**
     * 角色级别
     */
    private Integer level;

    /**
     * 数据范围
     */
    private DataPermissionEnum dataScope;

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

    public Boolean getEnabled() {
        return enabled;
    }

    public Role setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public DataPermissionEnum getDataScope() {
        return dataScope;
    }

    public void setDataScope(DataPermissionEnum dataScope) {
        this.dataScope = dataScope;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", level=" + level +
                ", dataScope=" + dataScope +
                '}';
    }
}
