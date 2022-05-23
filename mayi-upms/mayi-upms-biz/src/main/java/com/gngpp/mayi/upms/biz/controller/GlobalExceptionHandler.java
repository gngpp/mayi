/*
 *
 *  * Copyright (c) 2021 gngpp
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

package com.gngpp.mayi.upms.biz.controller;

import com.gngpp.mayi.common.core.foundation.DataResult;
import com.gngpp.mayi.common.core.foundation.exception.BusinessException;
import com.gngpp.mayi.upms.biz.security.backup.exception.SQLBackupException;
import com.gngpp.mayi.upms.biz.security.exception.SecurityException;
import com.gngpp.mayi.upms.biz.service.exception.SysBaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @author ant
 */
@RestControllerAdvice
@SuppressWarnings("rawtypes")
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger("[GlobalExceptionHandler]");

    /**
     * 全局异常类（拦截不到子类型处理）
     *
     * @param exception 异常
     * @return {@link DataResult}
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    DataResult exceptionHandler(Exception exception) {
        return DataResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
    }

    /**
     * 全局异常类（拦截不到子类型处理）
     *
     * @param exception 异常
     * @return {@link DataResult}
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    DataResult runtimeExceptionHandler(Exception exception) {
        return DataResult.fail(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }


    /**
     * 安全管理异常拦截处理
     *
     * @param exception 异常
     * @return {@link DataResult}
     * @date 2021-05-12 08:53:14
     */
    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    DataResult handleSecurityException(SecurityException exception) {
        return DataResult.fail(exception.getValue(), exception.getMessage());
    }

    /**
     * 业务异常类
     *
     * @param exception 异常
     * @return {@link DataResult}
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    DataResult badBusinessExceptionHandler(BusinessException exception) {
        return DataResult.fail(exception.getValue(), exception.getReasonPhrase());
    }

    /**
     * 方法参数异常
     *
     * @param exception 异常
     * @return {@link DataResult}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    DataResult validateExceptionHandler(MethodArgumentNotValidException exception) {
        String messages = exception.getBindingResult()
                                   .getAllErrors()
                                   .stream()
                                   .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                   .collect(Collectors.joining(","));
        return DataResult.fail(HttpStatus.BAD_REQUEST.value(), messages);
    }

    /**
     * 后台系统业务异常
     *
     * @param exception 异常
     * @return {@link DataResult}
     */
    @ExceptionHandler(SysBaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    DataResult SysBaseExceptionHandler(SysBaseException exception) {
        String message;
        if (exception.getLabel() != null) {
            message = MessageFormatter.format(exception.getReasonPhrase(), exception.getLabel()).getMessage();
        } else {
            message = exception.getReasonPhrase();
        }
        return DataResult.fail(exception.getValue(), message);
    }

    /**
     * 数据库备份异常
     *
     * @param exception 备份异常
     * @return {@link DataResult}
     */
    @ExceptionHandler(SQLBackupException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    DataResult handleSessionException(SQLBackupException exception) {
        return DataResult.fail(exception);
    }

}
