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
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.commom.cache.handler;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * @author WINDOWS
 */
public class SpringElExpressionHandler {

    /**
     * SpEL表达式解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 参数匹配器
     */
    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 解析 Spring EL 表达式
     *
     * @param method        方法
     * @param arguments     参数
     * @param springEl      表达式
     * @param clazz         返回结果的类型
     * @param defaultResult 默认结果
     * @return 执行spring el表达式后的结果
     */
    public <T> T parse(Method method, Object[] arguments, String springEl, Class<T> clazz, T defaultResult) {
        String[] params = discoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        int paramsLength = Objects.requireNonNull(params).length;
        for (int len = 0; len < paramsLength; len++) {
            context.setVariable(params[len], arguments[len].toString());
        }
        try {
            Expression expression = parser.parseExpression(springEl);
            return Optional.ofNullable(expression.getValue(context, clazz)).orElse(defaultResult);
        } catch (Exception ignored) {
            return defaultResult;
        }
    }

    /**
     * 筛选方法
     *
     * @param joinPoint 切点
     * @return method
     */
    public Method filterMethod(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        // 获取真实的调用对象，防止注解加在接口或抽象方法上
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(joinPoint.getTarget());
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // 防止桥接方法
        return BridgeMethodResolver.findBridgedMethod(specificMethod);
    }

}
