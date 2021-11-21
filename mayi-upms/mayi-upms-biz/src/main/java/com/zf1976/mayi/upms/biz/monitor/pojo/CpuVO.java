/*
 * Copyright (c) 2021 zf1976
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.upms.biz.monitor.pojo;


/**
 * @author mac
 * @date 2021/1/1
 **/
public class CpuVO {

    /**
     * 名称
     */
    private String name;

    /**
     * 物理cpu包数
     */
    private Integer physicalPackage;

    /**
     * 物理核心
     */
    private Integer core;

    /**
     * 逻辑核心
     */
    private Integer logic;

    /**
     * 使用
     */
    private String used;

    /**
     * 空闲
     */
    private String idle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPhysicalPackage() {
        return physicalPackage;
    }

    public void setPhysicalPackage(Integer physicalPackage) {
        this.physicalPackage = physicalPackage;
    }

    public Integer getCore() {
        return core;
    }

    public void setCore(Integer core) {
        this.core = core;
    }

    public Integer getLogic() {
        return logic;
    }

    public void setLogic(Integer logic) {
        this.logic = logic;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getIdle() {
        return idle;
    }

    public void setIdle(String idle) {
        this.idle = idle;
    }

    @Override
    public String toString() {
        return "CpuVO{" +
                "name='" + name + '\'' +
                ", physicalPackage=" + physicalPackage +
                ", core=" + core +
                ", logic=" + logic +
                ", used='" + used + '\'' +
                ", idle='" + idle + '\'' +
                '}';
    }
}
