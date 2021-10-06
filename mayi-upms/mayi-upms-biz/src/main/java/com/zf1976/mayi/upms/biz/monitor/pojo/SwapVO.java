package com.zf1976.mayi.upms.biz.monitor.pojo;


/**
 * @author mac
 * @date 2021/1/1
 **/
public class SwapVO {

    /**
     * 总的
     */
    private String total;

    /**
     * 空闲
     */
    private String available;

    /**
     * 使用
     */
    private String used;

    /**
     * 使用率
     */
    private String usageRate;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getUsageRate() {
        return usageRate;
    }

    public void setUsageRate(String usageRate) {
        this.usageRate = usageRate;
    }

    @Override
    public String toString() {
        return "SwapVO{" +
                "total='" + total + '\'' +
                ", available='" + available + '\'' +
                ", used='" + used + '\'' +
                ", usageRate='" + usageRate + '\'' +
                '}';
    }
}
