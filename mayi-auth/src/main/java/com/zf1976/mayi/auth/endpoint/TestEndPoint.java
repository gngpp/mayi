package com.zf1976.mayi.auth.endpoint;

import com.zf1976.mayi.auth.oauth2.repository.CustomizeJdbcRegisteredClientRepository;
import com.zf1976.mayi.auth.oauth2.repository.CustomizeRegisteredClientRepository;
import com.zf1976.mayi.auth.oauth2.repository.Page;
import com.zf1976.mayi.auth.pojo.*;
import com.zf1976.mayi.auth.pojo.vo.RegisteredClientVO;
import com.zf1976.mayi.auth.service.OAuth2RegisteredClientService;
import com.zf1976.mayi.common.core.foundation.DataResult;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mac
 * 2021/11/14 星期日 12:26 上午
 */
@RestController
@RequestMapping("/api/test")
public class TestEndPoint {

    private final CustomizeRegisteredClientRepository customizeRegisteredClientRepository;

    private final OAuth2RegisteredClientService oAuth2RegisteredClientService;

    public TestEndPoint(JdbcOperations jdbcOperations, OAuth2RegisteredClientService oAuth2RegisteredClientService) {
        this.customizeRegisteredClientRepository = new CustomizeJdbcRegisteredClientRepository(jdbcOperations);
        this.oAuth2RegisteredClientService = oAuth2RegisteredClientService;
    }

    @PostMapping("/messages")
    public String[] getMessages() {
        return new String[]{"Message 1", "Message 2", "Message 3"};
    }

    @PostMapping("/client")
    public DataResult<Page<RegisteredClientVO>> client(@RequestBody Page<?> registeredClientPage){
        return DataResult.success(this.oAuth2RegisteredClientService.findList(registeredClientPage));
    }

    @GetMapping("/testTransaction")
    public DataResult<Void> testTransaction(@RequestParam String clientId) {
        return DataResult.success(this.oAuth2RegisteredClientService.deleteById(clientId));
    }

}
