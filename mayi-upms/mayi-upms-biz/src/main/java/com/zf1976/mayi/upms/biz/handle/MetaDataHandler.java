package com.zf1976.mayi.upms.biz.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zf1976.mayi.upms.biz.security.Context;
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
        String currentUsername = Context.getUsername();
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("createBy", currentUsername, metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", currentUsername, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String currentUsername = Context.getUsername();
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", currentUsername, metaObject);
    }


}
