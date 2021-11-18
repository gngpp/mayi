package com.zf1976.mayi.upms.biz.pojo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author mac
 * @date 2021/1/24
 **/
public enum LogType implements IEnum<Integer> {

    /**
     * 操作日志
     */
    INFO(0,"操作日志"),

    /**
     * 系统日志
     */
    SYSTEM(1,"系统日志"),

    /**
     * 错误日志
     */
    ERROR(2, "错误日志"),

    /**
     * 未知描述日志
     */
    FOUND_DESCRIPTION(3,"未知日志");

    @EnumValue
    public final int value;

    @JsonValue
    public final String description;

    LogType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.description;
    }

}
