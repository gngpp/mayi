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

import com.gngpp.mayi.upms.biz.pojo.enums.DataPermissionEnum;

import java.io.Serializable;

/**
 * @author ant
 * Create by Ant on 2020/9/8 9:02 下午
 */
public class Role implements Serializable {

    private static final long serialVersionUID = 4704446806397632861L;
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
