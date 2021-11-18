package com.zf1976.mayi.upms.biz.pojo.query;

import com.zf1976.mayi.upms.biz.pojo.enums.LogType;

import java.util.Date;
import java.util.List;

/**
 * @author mac
 * @date 2021/1/26
 **/
public class LogQueryParam extends AbstractQueryParam {

    /**
     * 模糊搜索
     */
    private String blurry;

    /**
     * 创建时间
     */
    private List<Date> createTime;

    /**
     * 日志类型查询参数
     */
    LogType logType;

    public String getBlurry() {
        return blurry;
    }

    public LogQueryParam setBlurry(String blurry) {
        this.blurry = blurry;
        return this;
    }

    public List<Date> getCreateTime() {
        return createTime;
    }

    public LogQueryParam setCreateTime(List<Date> createTime) {
        this.createTime = createTime;
        return this;
    }

    public LogType getLogType() {
        return logType;
    }

    public LogQueryParam setLogType(LogType logType) {
        this.logType = logType;
        return this;
    }

    @Override
    public String toString() {
        return "LogQueryParam{" +
                "blurry='" + blurry + '\'' +
                ", createTime=" + createTime +
                ", logType=" + logType +
                '}';
    }
}
