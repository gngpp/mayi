package com.zf1976.mayi.upms.biz.convert;

import com.zf1976.mayi.upms.biz.pojo.po.SysLog;
import com.zf1976.mayi.upms.biz.pojo.vo.ErrorLogVO;
import com.zf1976.mayi.upms.biz.pojo.vo.LogVO;
import com.zf1976.mayi.upms.biz.pojo.vo.UserLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2021/1/26
 **/
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysLogConvert {

    SysLogConvert INSTANCE = Mappers.getMapper(SysLogConvert.class);

    /**
     * to vo
     *
     * @param sysLog log
     * @return /
     */
    LogVO toVo(SysLog sysLog);

    /**
     * to vo
     *
     * @param sysLog sysLog
     * @return /
     */
    ErrorLogVO toErrorVo(SysLog sysLog);

    /**
     * to vo
     *
     * @param sysLog sysLog
     * @return /
     */
    UserLogVO toUserLogVo(SysLog sysLog);
}
