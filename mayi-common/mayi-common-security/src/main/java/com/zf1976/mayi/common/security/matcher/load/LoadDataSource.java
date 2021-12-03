/*
 *
 *  * Copyright (c) 2021 zf1976
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *  *
 *
 */

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
     * 加载请求map
     *
     * @return {@link Map<RequestMatcher, Collection<ConfigAttribute>>}
     */
    Map<RequestMatcher, Collection<ConfigAttribute>> loadRequestMap();

    /**
     * 加载黑名单请求
     *
     * @return {@link Collection<RequestMatcher>}
     * @date 2021-05-05 19:53:43
     */
    Collection<RequestMatcher> loadBlackListRequest();

    /**
     * 加载放行请求
     *
     * @return {@link Collection<RequestMatcher>}
     */
    Collection<RequestMatcher> loadAllowRequest();

}
