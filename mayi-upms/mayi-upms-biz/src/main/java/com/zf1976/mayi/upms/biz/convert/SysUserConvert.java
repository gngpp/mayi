package com.zf1976.mayi.upms.biz.convert;


import com.zf1976.mayi.upms.biz.convert.base.Convert;
import com.zf1976.mayi.upms.biz.pojo.User;
import com.zf1976.mayi.upms.biz.pojo.dto.user.UpdateInfoDTO;
import com.zf1976.mayi.upms.biz.pojo.dto.user.UserDTO;
import com.zf1976.mayi.upms.biz.pojo.po.SysUser;
import com.zf1976.mayi.upms.biz.pojo.vo.user.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author Windows
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysUserConvert extends Convert<SysUser, UserVO, UserDTO> {
    SysUserConvert INSTANCE = Mappers.getMapper(SysUserConvert.class);

    /**
     * 复制属性
     *
     * @param source source
     * @param target target
     */
    void copyProperties(UserDTO source, @MappingTarget User target);

    /**
     * 复制属性
     *
     * @param dto source
     * @param user target
     */
    void copyProperties(UpdateInfoDTO dto, @MappingTarget User user);


    /**
     * 转 认证信息
     *
     * @param sysUser 系统用户
     * @return /
     */
    User convert(SysUser sysUser);
}
