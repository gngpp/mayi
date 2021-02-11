package com.zf1976.ant.common.security.safe.annotation;

import java.lang.annotation.*;

/**
 * mac
 * 2020/12/29
 * @author mac*/
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {
    String[] value() default {};
}
