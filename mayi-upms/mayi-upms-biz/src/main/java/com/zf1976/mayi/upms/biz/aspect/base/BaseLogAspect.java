package com.zf1976.mayi.upms.biz.aspect.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zf1976.mayi.common.core.util.RequestUtil;
import com.zf1976.mayi.common.core.util.StringUtil;
import com.zf1976.mayi.upms.biz.pojo.po.SysLog;
import com.zf1976.mayi.upms.biz.pojo.enums.LogType;
import com.zf1976.mayi.upms.biz.security.Context;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author mac
 * @date 2021/1/25
 **/
public abstract class BaseLogAspect {


    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected SysLog logBuilder(JoinPoint joinPoint, String description, LogType logType) throws JsonProcessingException {
        long start = System.currentTimeMillis();
        // 获取当前请求对象
        HttpServletRequest request = RequestUtil.getRequest();
        // result
        Object proceed = joinPoint.getTarget();
        // 方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 请求方法
        Method method = methodSignature.getMethod();
        // 类名
        String className = proceed.getClass().getName();
        // 方法名
        String methodSignatureName = methodSignature.getName();
        // 调用者
        String username = Context.getUsername();

        SysLog sysLog = new SysLog();
        sysLog.setUsername(username)
              .setLogType(logType)
              .setDescription(description)
              .setClassName(className)
              .setMethodName(methodSignatureName)
              .setRequestMethod(request.getMethod())
              .setUri(request.getRequestURI())
              .setIp(RequestUtil.getIpAddress())
              .setIpRegion(RequestUtil.getIpRegion())
              .setParameter(this.toJsonString(this.getParameters(method, joinPoint.getArgs())))
              .setUserAgent(RequestUtil.getUserAgent())
              .setSpendTime((int) (System.currentTimeMillis() - start))
              .setCreateTime(new Date(start));
        return sysLog;
    }

    /**
     * 转json
     *
     * @param o object
     * @return json
     */
    private String toJsonString(Object o) {
        try {
            return this.objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return StringUtil.ENMPTY;
        }
    }


    /**
     * 获取请求参数
     *
     * @param method method
     * @param arguments arguments
     * @return /
     */
    private Object getParameters(Method method, Object[] arguments) {
        List<Object> argumentList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < method.getParameters().length; i++) {
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                argumentList.add(arguments[i]);
                continue;
            }
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null) {
                HashMap<Object, Object> kv = new HashMap<>(16);
                String key = parameters[i].getName();
                if (!StringUtil.isEmpty(requestParam.value())) {
                    key = requestParam.value();
                }
                kv.put(key, arguments[i]);
                argumentList.add(kv);
                continue;
            }
            PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                argumentList.add(arguments[i]);
                continue;
            }
            argumentList.add(parameters[i]);
        }
        if (CollectionUtils.isEmpty(argumentList)) {
            return Collections.emptyList();
        }
        return argumentList;
    }

}
