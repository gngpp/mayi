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
