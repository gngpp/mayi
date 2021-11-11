package com.zf1976.mayi.auth.enhance;

//import com.zf1976.mayi.common.component.cache.annotation.CacheConfig;
//import com.zf1976.mayi.common.security.support.signature.datasource.ClientDataSourceProvider;
//import com.zf1976.mayi.common.security.support.signature.datasource.domain.Client;
//import com.zf1976.mayi.common.component.cache.annotation.CacheEvict;
//import com.zf1976.mayi.common.component.cache.annotation.CachePut;
//import com.zf1976.mayi.common.core.constants.Namespace;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
//import org.springframework.security.oauth2.common.util.JdbcListFactory;
//import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
//import org.springframework.security.oauth2.provider.ClientDetails;
//import org.springframework.security.oauth2.provider.NoSuchClientException;
//import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
//import org.springframework.stereotype.Service;
//
//import javax.sql.DataSource;
//import java.util.List;
//
///**
// * @author mac
// * @date 2021/3/5
// **/
//@Service
//@CacheConfig(namespace = Namespace.CLIENT)
//public class JdbcClientDetailsServiceEnhancer extends JdbcClientDetailsService implements ClientDataSourceProvider {
//
//    public JdbcClientDetailsServiceEnhancer(DataSource dataSource) {
//        super(dataSource);
//    }
//
//    @Override
//    @CachePut(key = "#clientId")
//    public Client selectClientByClientId(String clientId) {
//        // 查询客户端
//        var clientDetails = super.loadClientByClientId(clientId);
//        var client = new Client();
//        if (clientDetails != null) {
//            client.setClientId(clientDetails.getClientId())
//                  .setClientSecret(clientDetails.getClientSecret());
//        }
//        return client;
//    }
//}
