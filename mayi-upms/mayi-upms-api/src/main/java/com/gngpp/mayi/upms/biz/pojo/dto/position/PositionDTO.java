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

package com.gngpp.mayi.upms.biz.pojo.dto.position;

import com.gngpp.mayi.common.core.validate.ValidationInsertGroup;
import com.gngpp.mayi.common.core.validate.ValidationUpdateGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

/**
 * @author mac
 * @date 2020/10/25 5:08 下午
 */
public class PositionDTO {

    /**
     * id
     */
    @Null(groups = ValidationInsertGroup.class)
    @NotNull(groups = ValidationUpdateGroup.class)
    private Long id;

    /**
     * 岗位名称
     */
    @NotBlank
    private String name;

    /**
     * 岗位状态
     */
    @NotNull
    private Boolean enabled;

    /**
     * 排序
     */
    @NotNull
    private Integer jobSort;

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getJobSort() {
        return jobSort;
    }

    public void setJobSort(Integer jobSort) {
        this.jobSort = jobSort;
    }

    @Override
    public String toString() {
        return "PositionDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", jobSort=" + jobSort +
                '}';
    }
}
