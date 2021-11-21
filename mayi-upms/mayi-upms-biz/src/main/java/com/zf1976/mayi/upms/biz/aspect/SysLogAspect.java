/*
 * Copyright (c) 2021 zf1976
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.upms.biz.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zf1976.mayi.common.core.util.ExceptionUtils;
import com.zf1976.mayi.common.core.util.StringUtil;
import com.zf1976.mayi.upms.biz.annotation.Log;
import com.zf1976.mayi.upms.biz.aspect.base.BaseLogAspect;
import com.zf1976.mayi.upms.biz.dao.SysLogDao;
import com.zf1976.mayi.upms.biz.pojo.po.SysLog;
import com.zf1976.mayi.upms.biz.pojo.enums.LogType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
/**
 * @author mac
 * @date 2020/12/24
 **/
@Aspect
@Component
public class SysLogAspect extends BaseLogAspect {

    private final Logger log = LoggerFactory.getLogger("[SysLogAspect-Log]");
    private final SysLogDao sysLogDao;
    private static final ThreadLocal<Boolean> RECORD = ThreadLocal.withInitial(() -> false);
    public SysLogAspect(SysLogDao sysLogDao) {
        this.sysLogDao = sysLogDao;
    }

    /**
     * 自定义日志切点
     */
    @Pointcut("@annotation(com.zf1976.mayi.upms.biz.annotation.Log)")
    public void restfulLog() {}

    /**
     * 全局日志切点
     */
    @Pointcut("execution(* com.zf1976.mayi.*.*.controller..*.*(..))")
    public void globalLog(){}

    /**
     * 环切 自定义日志
     *
     * @param joinPoint 切点
     * @return /
     */
    @Around(("restfulLog()&&@annotation(annotation)"))
    public Object doAround(ProceedingJoinPoint joinPoint, Log annotation) throws Throwable {
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            // 发生异常，日志已保存
            if (RECORD.get()) {
                RECORD.remove();
                throw throwable;
            } else {
                // 再次尝试保存日志
                this.doAfterThrowing(joinPoint, throwable);
            }
            RECORD.remove();
            throw throwable;
        }
        // 未发生异常且未保存日志
        if (!RECORD.get()) {
            SysLog sysLog;
            if (StringUtil.isEmpty(annotation.description())) {
                sysLog = super.logBuilder(joinPoint, LogType.FOUND_DESCRIPTION.description, LogType.FOUND_DESCRIPTION);
            } else {
                sysLog = super.logBuilder(joinPoint, annotation.description(), LogType.INFO);
            }
            if (!this.saveLog(sysLog)) {
                log.error("info log sava error！");
            }
        }
        RECORD.remove();
        return result;
    }


    /**
     * 全局异常后处理
     *
     * @param joinPoint joinPoint
     * @param e exception
     */
    @AfterThrowing(pointcut = "globalLog()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) throws JsonProcessingException {
        Log annotation = this.getMethodLogAnnotation(joinPoint);
        String traceAsString = ExceptionUtils.getStackTraceAsString(e);
        String exceptionDetail = objectMapper.writeValueAsString(traceAsString);
        SysLog sysLog;
        if (annotation != null) {
            sysLog = super.logBuilder(joinPoint, annotation.description(), LogType.ERROR)
                          .setExceptionDetails(exceptionDetail);
        } else {
            sysLog = super.logBuilder(joinPoint, e.getMessage(), LogType.ERROR)
                          .setExceptionDetails(exceptionDetail);
        }
        // 保存日志是否保存成功
        if (this.saveLog(sysLog)) {
            RECORD.set(true);
        }
    }

    /**
     * 保存日志
     *
     * @param sysLog sysLog
     * @return /
     */
    private boolean saveLog(SysLog sysLog) {
        return this.sysLogDao.insert(sysLog) > 0;
    }


    /**
     * 获取方法Log注解
     *
     * @param joinPoint joinPoint
     * @return /
     */
    protected Log getMethodLogAnnotation(JoinPoint joinPoint) {
        // 方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 方法
        Method method = methodSignature.getMethod();
        // 返回方法上注解
        return method.getAnnotation(Log.class);
    }

}
