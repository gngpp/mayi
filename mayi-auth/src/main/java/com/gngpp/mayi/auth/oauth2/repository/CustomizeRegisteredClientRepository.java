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

package com.gngpp.mayi.auth.oauth2.repository;

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
     * remove client by client_id list
     *
     * @param ids client id list
     */
    void deleteByClientIds(List<String> ids);

    /**
     * find client list
     *
     * @return the {@link List<RegisteredClient>}
     */
    Page<RegisteredClient> findClientList(Page<?> page);

}
