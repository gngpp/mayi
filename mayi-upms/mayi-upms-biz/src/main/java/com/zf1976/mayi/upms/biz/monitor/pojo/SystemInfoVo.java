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
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.upms.biz.monitor.pojo;


/**
 * @author mac
 * @date 2021/1/1
 **/
public class SystemInfoVo {

    private OperatingSystemVO operatingSystem;

    private CpuVO cpu;

    private MemoryVO memory;

    private SwapVO swap;

    private DiskVO disk;

    private String datetime;

    public OperatingSystemVO getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(OperatingSystemVO operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public CpuVO getCpu() {
        return cpu;
    }

    public void setCpu(CpuVO cpu) {
        this.cpu = cpu;
    }

    public MemoryVO getMemory() {
        return memory;
    }

    public void setMemory(MemoryVO memory) {
        this.memory = memory;
    }

    public SwapVO getSwap() {
        return swap;
    }

    public void setSwap(SwapVO swap) {
        this.swap = swap;
    }

    public DiskVO getDisk() {
        return disk;
    }

    public void setDisk(DiskVO disk) {
        this.disk = disk;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "SystemInfoVo{" +
                "operatingSystem=" + operatingSystem +
                ", cpu=" + cpu +
                ", memory=" + memory +
                ", swap=" + swap +
                ", disk=" + disk +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}
