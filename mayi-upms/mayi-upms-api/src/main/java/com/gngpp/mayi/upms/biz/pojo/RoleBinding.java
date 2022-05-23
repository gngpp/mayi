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

import java.io.Serializable;
import java.util.List;

/**
 * @author mac
 * @date 2021/5/29
 */
public class RoleBinding implements Serializable {

    private static final long serialVersionUID = 1484169433398768663L;

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 数据权限
     */
    private Integer dataScope;

    /**
     * 状态
     */
    private Boolean enabled;

    /**
     * 权限列表
     */
    private List<Permission> bindingPermissions;

    public Long getId() {
        return id;
    }

    public RoleBinding setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getDataScope() {
        return dataScope;
    }

    public RoleBinding setDataScope(Integer dataScope) {
        this.dataScope = dataScope;
        return this;
    }

    public String getName() {
        return name;
    }

    public RoleBinding setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RoleBinding setDescription(String description) {
        this.description = description;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public RoleBinding setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public List<Permission> getBindingPermissions() {
        return bindingPermissions;
    }

    public RoleBinding setBindingPermissions(List<Permission> bindingPermissions) {
        this.bindingPermissions = bindingPermissions;
        return this;
    }

    @Override
    public String toString() {
        return "RoleBinding{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dataScope=" + dataScope +
                ", enabled=" + enabled +
                ", bindingPermissions=" + bindingPermissions +
                '}';
    }
}
