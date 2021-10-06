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
