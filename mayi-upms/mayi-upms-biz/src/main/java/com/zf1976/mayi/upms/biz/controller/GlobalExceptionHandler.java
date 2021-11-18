package com.zf1976.mayi.upms.biz.controller;

import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.foundation.exception.BusinessException;
import com.zf1976.mayi.upms.biz.security.backup.exception.SQLBackupException;
import com.zf1976.mayi.upms.biz.security.exception.SecurityException;
import com.zf1976.mayi.upms.biz.service.exception.SysBaseException;
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
        return DataResult.fail(messages);
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
