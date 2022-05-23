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

package com.gngpp.mayi.upms.biz.monitor.sokcet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.gngpp.mayi.upms.biz.monitor.pojo.SystemInfoVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author mac
 * @date 2021/1/23
 **/
@ServerEndpoint("/api/monitor")
@Component
public class MonitorWebSocket {

    private final Logger log = LoggerFactory.getLogger("[MonitorWenSocket]");
    private static final Map<String, Session> CLIENT_SESSION = new ConcurrentHashMap<>();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
            new ThreadFactoryBuilder().setNameFormat("monitor websocket thread").build());


    public MonitorWebSocket() {
        // 每秒进行群发一次
        scheduledExecutorService.scheduleWithFixedDelay(this::sendSystemInfo,
                0,
                1,
                TimeUnit.SECONDS);
    }

    /**
     * 发送消息
     */
    private void sendSystemInfo() {
        if (CLIENT_SESSION.size() > 0) {
            sendAll(MonitorUtils.getSystemInfo());
        }
    }

    /**
     * 新建链接
     *
     * @param session session
     */
    @OnOpen
    public void onOpen(Session session) throws JsonProcessingException {
        log.info("new session id：{}", session.getId());
        CLIENT_SESSION.put(session.getId(), session);
        final String result = MAPPER.writeValueAsString(MonitorUtils.getSystemInfo());
        session.getAsyncRemote()
               .sendText(result);
    }

    /**
     * 发生错误
     *
     * @param throwable throwable
     */
    @OnError
    public void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }

    /**
     * 收到客户端发来消息
     *
     * @param message 消息对象
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("websocket receives a message from the client：{}", message);
    }

    /**
     * 链接关闭
     *
     * @param session session
     */
    @OnClose
    public void onClose(Session session) {
        log.info("a user is disconnected, id：{}", session.getId());
        CLIENT_SESSION.remove(session.getId());
    }

    /**
     * 群发消息
     *
     * @param systemInfoVo 消息内容
     */
    private void sendAll(SystemInfoVo systemInfoVo) {
        for (Map.Entry<String, Session> entry : CLIENT_SESSION.entrySet()) {
            try {
                final String result = MAPPER.writeValueAsString(systemInfoVo);
                entry.getValue()
                     .getAsyncRemote()
                     .sendText(result);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e.getCause());
            }
        }
    }

}
