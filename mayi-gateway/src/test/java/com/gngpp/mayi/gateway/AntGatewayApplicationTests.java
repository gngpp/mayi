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

package com.gngpp.mayi.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Objects;

@SpringBootTest
class AntGatewayApplicationTests {

    @Test
    void contextLoads() {
        HttpStatus httpStatus = Objects.requireNonNull(WebClient.create()
                                                                .get()
                                                                .uri("http://localhost:9000/oauth/check_token", uriBuilder -> uriBuilder.queryParam("token", "fucking")
                                                                                                                                        .build())
                                                                .exchange()
                                                                .block(Duration.ofSeconds(10)))
                                       .statusCode();
        if (!httpStatus.is2xxSuccessful()) {
            System.out.println("error");
        }

        ClientResponse block = WebClient.create("http://localhost:9000/oauth/check_token")
                                        .get()
                                        .uri(uriBuilder -> {
                                            return uriBuilder.queryParam("token", "hanbi")
                                                             .build();
                                        })
                                        .exchange()
                                        .doOnSuccess(clientResponse -> {
                                            if (clientResponse.statusCode()
                                                              .is2xxSuccessful()) {

                                            }
                                        })
                                        .block(Duration.ofSeconds(5));
        if (block != null && block.statusCode().is2xxSuccessful()) {

        }
    }

}
