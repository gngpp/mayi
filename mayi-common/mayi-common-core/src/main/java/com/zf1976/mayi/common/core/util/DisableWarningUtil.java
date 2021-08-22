package com.zf1976.mayi.common.core.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author mac
 * 2021/8/22 星期日 8:35 下午
 */
public class DisableWarningUtil {

    /**
     * 关闭runtime warnings
     */
    public static void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            unsafe.putObjectVolatile(cls, unsafe.staticFieldOffset(logger), null);
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
