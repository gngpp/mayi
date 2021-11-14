package com.zf1976.mayi.auth.endpoint;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mac
 * 2021/11/14 星期日 12:26 上午
 */
@RestController
@RequestMapping("/api/test")
public class TestEndPoint {

    @PostMapping("/messages")
    public String[] getMessages() {
        return new String[]{"Message 1", "Message 2", "Message 3"};
    }

}
