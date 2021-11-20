package com.zf1976.mayi.auth.oauth2.repository;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.List;

/**
 * @author ant
 * Create by Ant on 2021/11/19 00:39
 */
public interface CustomizeRegisteredClientRepository extends RegisteredClientRepository {

    /**
     * Saves the registered client.
     *
     * @param registeredClient the {@link RegisteredClient}
     */
    @Override
    void save(RegisteredClient registeredClient);

    /**
     * Returns the registered client identified by the provided {@code id},
     * or {@code null} if not found.
     *
     * @param id the registration identifier
     * @return the {@link RegisteredClient} if found, otherwise {@code null}
     */
    @Override
    RegisteredClient findById(String id);

    /**
     * Returns the registered client identified by the provided {@code clientId},
     * or {@code null} if not found.
     *
     * @param clientId the client identifier
     * @return the {@link RegisteredClient} if found, otherwise {@code null}
     */
    @Override
    RegisteredClient findByClientId(String clientId);

    /**
     * id
     *
     * @param id id
     */
    void deleteById(String id);

    /**
     * client id
     *
     * @param clientId clientId
     */
    void deleteByClientId(String clientId);


    /**
     * remove client by id list
     *
     * @param ids id list
     */
    void deleteByIds(List<String> ids);

    /**
     * find client list
     *
     * @return the {@link List<RegisteredClient>}
     */
    Page<RegisteredClient> findClientList(Page<?> page);

}
