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

package com.zf1976.mayi.upms.biz.pojo.vo.dept;


import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author mac
 */
public class DepartmentExcelVO implements Serializable {

    private static final long serialVersionUID = -1092485164722322926L;

    /**
     * 上级部门
     */
    private Long pid;

    /**
     * 子部门数目
     */
    private Integer subCount;

    /**
     * 子部门
     */
    private Collection<Map<String, Object>> children;

    /**
     * 名称
     */
    private String name;

    /**
     * 状态
     */
    private Boolean enabled;


    /**
     * 创建日期
     */
    private Date createTime;

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Integer getSubCount() {
        return subCount;
    }

    public void setSubCount(Integer subCount) {
        this.subCount = subCount;
    }

    public Collection<Map<String, Object>> getChildren() {
        return children;
    }

    public void setChildren(Collection<Map<String, Object>> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return "DepartmentExcelVO{" +
                "pid=" + pid +
                ", subCount=" + subCount +
                ", children=" + children +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                '}';
    }
}
