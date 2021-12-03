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

package com.zf1976.mayi.upms.biz.pojo.query;

import com.zf1976.mayi.upms.biz.pojo.query.annotation.Param;
import com.zf1976.mayi.upms.biz.pojo.query.enmus.Type;

import java.util.Date;
import java.util.List;

/**
 * @author mac
 * @date 2020/10/25 5:12 下午
 */
public class PositionQueryParam extends AbstractQueryParam {

    private static final long serialVersionUID = 5373327350088419647L;

    /**
     * like
     */
    @Param(type = Type.LIKE)
    private String name;

    /**
     *  eq
     */
    @Param(type = Type.EQ)
    private Boolean enabled;

    /**
     * between
     */
    @Param(type = Type.BETWEEN)
    private List<Date> createTime;

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

    public List<Date> getCreateTime() {
        return createTime;
    }

    public void setCreateTime(List<Date> createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "PositionQueryParam{" +
                "name='" + name + '\'' +
                ", enabled=" + enabled +
                ", createTime=" + createTime +
                '}';
    }
}
