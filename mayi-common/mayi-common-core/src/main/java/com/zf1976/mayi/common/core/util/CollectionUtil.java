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

package com.zf1976.mayi.common.core.util;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author mac
 * 2021/12/1 星期三 2:07 PM
 */
public class CollectionUtil {

    /**
     * 判断两个{@link Set} 是否相同
     *
     * @param one one
     * @param two two
     * @return boolean
     */
    public static boolean notEq(Set<Long> one, Set<Long> two) {
        Assert.notNull(one, "list cannot been null");
        Assert.notNull(two, "list cannot been null");
        if (one.size() != two.size()) {
            return true;
        }
        Map<Long, Long> map = new HashMap<>(one.size());
        for (Long oneKey : one) {
            map.put(oneKey, 0L);
        }
        for (Long twoKey : two) {
            Long cc = map.get(twoKey);
            if (null != cc) {
                continue;
            }
            return true;
        }
        return false;
    }

}
