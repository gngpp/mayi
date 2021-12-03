/*
 *
 *  * Copyright (c) 2021 zf1976
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

package com.zf1976.mayi.upms.biz.pojo.vo.menu;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author mac
 * @date 2020/11/12
 **/
public class MenuVO implements Serializable {

    private static final long serialVersionUID = 3832534476704575642L;

    /**
     * id
     */
    private Long id;

    /**
     * 子节点
     */
    private List<MenuVO> children;

    /**
     * 上级菜单ID
     */
    private Long pid;

    /**
     * 菜单类型
     */
    private Integer type;

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 组件名称
     */
    private String componentName;

    /**
     * 组件路径
     */
    private String componentPath;

    /**
     * 排序
     */
    private Integer menuSort;

    /**
     * 是否存在子菜单
     */
    private Boolean hasChildren;

    /**
     * 是否叶子
     */
    private Boolean leaf;

    /**
     * 图标
     */
    private String icon;

    /**
     * 路由地址
     */
    private String routePath;

    /**
     * 是否外链
     */
    private Boolean iframe;

    /**
     * 缓存
     */
    private Boolean cache;

    /**
     * 隐藏
     */
    private Boolean hidden;

    /**
     * 权限
     */
    private String permission;

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

    public List<MenuVO> getChildren() {
        return children;
    }

    public void setChildren(List<MenuVO> children) {
        this.children = children;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentPath() {
        return componentPath;
    }

    public void setComponentPath(String componentPath) {
        this.componentPath = componentPath;
    }

    public Integer getMenuSort() {
        return menuSort;
    }

    public void setMenuSort(Integer menuSort) {
        this.menuSort = menuSort;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }

    public Boolean getIframe() {
        return iframe;
    }

    public void setIframe(Boolean iframe) {
        this.iframe = iframe;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "MenuVO{" +
                "id=" + id +
                ", children=" + children +
                ", pid=" + pid +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", componentName='" + componentName + '\'' +
                ", componentPath='" + componentPath + '\'' +
                ", menuSort=" + menuSort +
                ", hasChildren=" + hasChildren +
                ", leaf=" + leaf +
                ", icon='" + icon + '\'' +
                ", routePath='" + routePath + '\'' +
                ", iframe=" + iframe +
                ", cache=" + cache +
                ", hidden=" + hidden +
                ", permission='" + permission + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
