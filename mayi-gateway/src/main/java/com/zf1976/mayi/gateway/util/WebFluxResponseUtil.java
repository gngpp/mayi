/*
 *
 *  * Copyright (c) 2021 zf1976
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

package com.zf1976.mayi.gateway.util;

import com.zf1976.mayi.common.core.util.JSONUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author ant
 * Create by Ant on 2021/3/7 3:42 PM
 */
@SuppressWarnings({"rawtypes", "unused"})
public class WebFluxResponseUtil {

    public static Mono<Void> responseWriter(ServerWebExchange exchange, int httpStatus, String msg) {
        ResponseEntity<String> body = ResponseEntity.ok(msg);
        return responseWrite(exchange, httpStatus, body);
    }

    public static Mono<Void> responseFailed(ServerWebExchange exchange, String msg) {
        ResponseEntity<String> body = ResponseEntity.badRequest()
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .body(msg);
        return responseWrite(exchange, HttpStatus.INTERNAL_SERVER_ERROR.value(), body);
    }

    public static Mono<Void> responseFailed(ServerWebExchange exchange, int httpStatus, String msg) {
        ResponseEntity<String> body = ResponseEntity.badRequest()
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .body(msg);
        return responseWrite(exchange, httpStatus, body);
    }

    public static Mono<Void> responseWrite(ServerWebExchange exchange, int httpStatus, ResponseEntity result) {
        if (httpStatus == 0) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setAccessControlAllowCredentials(true);
        response.getHeaders().setAccessControlAllowOrigin("*");
        response.setStatusCode(HttpStatus.valueOf(httpStatus));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        DataBuffer buffer = dataBufferFactory.wrap(JSONUtil.toJsonString(result).getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer))
                       .doOnError((error) -> {
                           // 释放资源
                           DataBufferUtils.release(buffer);
                       });
    }
}
