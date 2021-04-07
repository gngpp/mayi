package com.zf1976.ant.auth.convert;

import com.zf1976.ant.common.security.pojo.UserInfo;
import com.zf1976.ant.upms.biz.pojo.po.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


/**
 * @author ant
 * Create by Ant on 2020/9/10 4:12 下午
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConversion {
    UserConversion INSTANCE = Mappers.getMapper(UserConversion.class);

    /**
     * 转 认证信息
     *
     * @param sysUser 系统用户
     * @return /
     */
    UserInfo convert(SysUser sysUser);
}
