package com.zf1976.mayi.auth.endpoint;

import com.zf1976.mayi.common.core.foundation.DataResult;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @author mac
 * @date 2021/5/7
 */
@SuppressWarnings("ALL")
@RestControllerAdvice
public class GlobalExceptionHandler {

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
                                   .collect(Collectors.joining(", "));
        return DataResult.fail(HttpStatus.BAD_REQUEST.value(), messages);
    }
}
