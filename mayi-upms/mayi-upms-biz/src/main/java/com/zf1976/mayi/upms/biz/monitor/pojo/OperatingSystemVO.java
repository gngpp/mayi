package com.zf1976.mayi.upms.biz.monitor.pojo;


/**
 * @author mac
 * @date 2021/1/1
 **/
public class OperatingSystemVO {

    private String os;

    private String runningDay;

    private String ip;

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getRunningDay() {
        return runningDay;
    }

    public void setRunningDay(String runningDay) {
        this.runningDay = runningDay;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "OperatingSystemVO{" +
                "os='" + os + '\'' +
                ", runningDay='" + runningDay + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
