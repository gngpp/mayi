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
 * @date 2021/5/6
 */
public class ResourceLinkBinding implements Serializable {

    private static final long serialVersionUID = -7875344596277114868L;

    private Long id;
    /**
     * 资源名称
     */
    private String name;
    /**
     * 资源url
     */
    private String uri;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 开关
     */
    private Boolean enabled;
    /**
     * 放行
     */
    private Boolean allow;
    /**
     * 绑定权限列表
     */
    private List<Permission> bindingPermissions;

    public Long getId() {
        return id;
    }

    public ResourceLinkBinding setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ResourceLinkBinding setName(String name) {
        this.name = name;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public ResourceLinkBinding setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public ResourceLinkBinding setMethod(String method) {
        this.method = method;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public ResourceLinkBinding setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Boolean getAllow() {
        return allow;
    }

    public ResourceLinkBinding setAllow(Boolean allow) {
        this.allow = allow;
        return this;
    }

    public List<Permission> getBindingPermissions() {
        return bindingPermissions;
    }

    public ResourceLinkBinding setBindingPermissions(List<Permission> bindingPermissions) {
        this.bindingPermissions = bindingPermissions;
        return this;
    }

    @Override
    public String toString() {
        return "ResourceLink{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", method='" + method + '\'' +
                ", enabled=" + enabled +
                ", allow=" + allow +
                ", bindingPermissions=" + bindingPermissions +
                '}';
    }
}
