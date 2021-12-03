package com.zf1976.mayi.common.security.matcher.load;

import com.zf1976.mayi.common.security.matcher.RequestMatcher;
import org.springframework.security.access.ConfigAttribute;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @author mac
 * 2021/12/3 星期五 8:51 PM
 */
public interface LoadDataSource extends Serializable {

    /**
     * 加载黑名单请求
     *
     * @return {@link Map}
     * @date 2021-05-05 19:53:43
     */
    Collection<RequestMatcher> loadBlackListRequest();


    /**
     * 加载请求map
     *
     * @return {@link Map<String,String>}
     */
    Map<RequestMatcher, Collection<ConfigAttribute>> loadRequestMap();

    /**
     * 加载放行请求
     *
     * @return {@link Collection<RequestMatcher>}
     */
    Collection<RequestMatcher> loadAllowRequest();

}
