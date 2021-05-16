package com.zf1976.ant.upms.biz.pojo.vo.dept;


import java.util.Collection;
import java.util.Date;

/**
 * @author mac
 * @date 2020/10/26 6:13 下午
 */
public class DepartmentVO {

    /**
     * id
     */
    private Long id;

    /**
     * 上级部门
     */
    private Long pid;

    /**
     * 子部门
     */
    private Collection<DepartmentVO> children;

    /**
     * 名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer deptSort;

    /**
     * 状态
     */
    private Boolean enabled;

    /**
     * 是否有下一级
     */
    private Boolean hasChildren;

    /**
     * 叶子
     */
    private Boolean leaf;

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

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Collection<DepartmentVO> getChildren() {
        return children;
    }

    public void setChildren(Collection<DepartmentVO> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDeptSort() {
        return deptSort;
    }

    public void setDeptSort(Integer deptSort) {
        this.deptSort = deptSort;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DepartmentVO{" +
                "id=" + id +
                ", pid=" + pid +
                ", children=" + children +
                ", name='" + name + '\'' +
                ", deptSort=" + deptSort +
                ", enabled=" + enabled +
                ", hasChildren=" + hasChildren +
                ", leaf=" + leaf +
                ", createTime=" + createTime +
                '}';
    }
}
