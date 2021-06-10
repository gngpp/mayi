package com.zf1976.mayi.upms.biz.convert;

import com.zf1976.mayi.common.security.support.session.Session;
import com.zf1976.mayi.upms.biz.pojo.vo.SessionVO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-06-10T14:08:13+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.7 (Oracle Corporation)"
)
public class SessionConvertImpl implements SessionConvert {

    @Override
    public SessionVO toVO(Session session) {
        if ( session == null ) {
            return null;
        }

        SessionVO sessionVO = new SessionVO();

        sessionVO.setId( session.getId() );
        sessionVO.setUsername( session.getUsername() );
        sessionVO.setIp( session.getIp() );
        sessionVO.setIpRegion( session.getIpRegion() );
        sessionVO.setBrowser( session.getBrowser() );
        sessionVO.setLoginTime( session.getLoginTime() );
        sessionVO.setExpiredTime( session.getExpiredTime() );

        return sessionVO;
    }
}