package com.zf1976.mayi.common.log.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zf1976.mayi.common.core.util.ExceptionUtils;
import com.zf1976.mayi.common.core.util.StringUtil;
import com.zf1976.mayi.common.log.annotation.Log;
import com.zf1976.mayi.common.log.aspect.base.BaseLogAspect;
import com.zf1976.mayi.common.log.dao.SysLogDao;
import com.zf1976.mayi.common.log.pojo.SysLog;
import com.zf1976.mayi.common.log.pojo.enums.LogType;
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
    @Pointcut("@annotation(com.zf1976.mayi.common.log.annotation.Log)")
    public void restfulLog() {}

    /**
     * 全局日志切点
     */
    @Pointcut("execution(* com.zf1976.ant.*.*.controller..*.*(..))")
    public void globalLog(){}

    /**
     * 环切 自定义日志
     *
     * @param joinPoint 切点
     * @return /
     * @throws Throwable throwable
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
     * @throws JsonProcessingException jsonProcessingException
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
