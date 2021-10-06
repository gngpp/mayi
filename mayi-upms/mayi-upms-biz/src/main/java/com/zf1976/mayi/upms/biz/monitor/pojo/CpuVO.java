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
