package com.zf1976.mayi.auth.endpoint;

import com.zf1976.mayi.auth.oauth2.repository.CustomizeRegisteredClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
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

    CustomizeRegisteredClientRepository customizeRegisteredClientRepository;

    public TestEndPoint(CustomizeRegisteredClientRepository customizeRegisteredClientRepository) {
        this.customizeRegisteredClientRepository = customizeRegisteredClientRepository;
    }

    @PostMapping("/messages")
    public String[] getMessages() {
        return new String[]{"Message 1", "Message 2", "Message 3"};
    }

    @GetMapping("/client")
    public Object client(){
        return this.customizeRegisteredClientRepository.findClientList(1, 5);
    }
}
