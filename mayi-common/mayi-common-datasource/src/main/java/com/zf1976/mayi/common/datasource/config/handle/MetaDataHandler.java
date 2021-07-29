package com.zf1976.mayi.common.datasource.config.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zf1976.mayi.common.security.support.session.manager.SessionManagement;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * 自动填充数据拦截器
 *
 * @author mac
 * @date 2021/2/8
 **/
public class MetaDataHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("createBy", this.getPrincipal(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", this.getPrincipal(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", this.getPrincipal(), metaObject);
    }

    private String getPrincipal() {
        return SessionManagement.getCurrentUsername();
    }

}
