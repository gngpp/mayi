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

package com.gngpp.mayi.upms.biz.pojo;

import com.gngpp.mayi.upms.biz.pojo.po.SysResource;

import java.io.Serializable;
import java.util.List;

/**
 * @author mac
 * @date 2021/5/6
 */
public class ResourceNode implements Serializable {

    private static final long serialVersionUID = 5183125845705437100L;

    private Long id;
    /**
     * pid resource
     */
    private Long pid;
    /**
     * 资源名称
     */
    private String name;
    /**
     * 资源url
     */
    private String uri;
    /**
     * 完整uri, 从父节点到当前节点到uri拼接
     */
    private String fullUri;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 开关
     */
    private Boolean enabled;
    /**
     * 资源描述
     */
    private String description;
    /**
     * 放行
     */
    private Boolean allow;
    /**
     * 是否为叶子
     */
    private Boolean leaf;

    private List<Permission> bindingPermissions;
    /**
     * 子节点
     */
    private List<ResourceNode> children;

    public ResourceNode() {}

    public ResourceNode(SysResource sysResource) {
        this.id = sysResource.getId();
        this.pid = sysResource.getPid();
        this.name = sysResource.getName();
        this.uri = sysResource.getUri();
        this.method = sysResource.getMethod();
        this.enabled = sysResource.getEnabled();
        this.leaf = sysResource.getLeaf();
        this.description = sysResource.getDescription();
        this.allow = sysResource.getAllow();
        this.bindingPermissions = sysResource.getBindingPermissions();
    }

    public List<Permission> getBindingPermissions() {
        return bindingPermissions;
    }

    public ResourceNode setBindingPermissions(List<Permission> bindingPermissions) {
        this.bindingPermissions = bindingPermissions;
        return this;
    }

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

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFullUri() {
        return fullUri;
    }

    public void setFullUri(String fullUri) {
        this.fullUri = fullUri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAllow() {
        return allow;
    }

    public void setAllow(Boolean allow) {
        this.allow = allow;
    }

    public List<ResourceNode> getChildren() {
        return children;
    }

    public void setChildren(List<ResourceNode> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "ResourceNode{" +
                "id=" + id +
                ", pid=" + pid +
                ", name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", method='" + method + '\'' +
                ", enabled=" + enabled +
                ", description='" + description + '\'' +
                ", allow=" + allow +
                ", children=" + children +
                '}';
    }
}
