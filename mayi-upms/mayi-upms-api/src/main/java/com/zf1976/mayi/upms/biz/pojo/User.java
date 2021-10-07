package com.zf1976.mayi.upms.biz.pojo;

import com.zf1976.mayi.upms.biz.pojo.enums.GenderEnum;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author ant
 * Create by Ant on 2020/9/8 12:32 下午
 */
public class User implements Serializable {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 部门
     */
    private Department department;

    /**
     * 角色信息
     */
    private Set<Role> roleList;

    /**
     * 职位信息
     */
    private Set<Position> positionList;

    /**
     * 权限
     */
    private Set<String> permissions;

    /**
     * 数据权限
     */
    private Set<Long> dataPermissions;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别
     */
    private GenderEnum gender;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像名
     */
    private String avatarName;

    /**
     * 密码
     */
    private String password;

    /**
     * 状态：1启用、0禁用
     */
    private Boolean enabled;

    /**
     * 创建日期
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(Set<Role> roleList) {
        this.roleList = roleList;
    }

    public Set<Position> getPositionList() {
        return positionList;
    }

    public void setPositionList(Set<Position> positionList) {
        this.positionList = positionList;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Set<Long> getDataPermissions() {
        return dataPermissions;
    }

    public void setDataPermissions(Set<Long> dataPermissions) {
        this.dataPermissions = dataPermissions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", department=" + department +
                ", roleList=" + roleList +
                ", positionList=" + positionList +
                ", permissions=" + permissions +
                ", dataPermissions=" + dataPermissions +
                ", username='" + username + '\'' +
                ", nickName='" + nickName + '\'' +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", avatarName='" + avatarName + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                '}';
    }
}
