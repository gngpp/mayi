/*
 * Copyright (c) 2021 zf1976
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.auth.oauth2.repository;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.List;

/**
 * @author ant
 * Create by Ant on 2021/11/19 00:34
 */
@SuppressWarnings("SameParameterValue")
public class CustomizeJdbcRegisteredClientRepository implements CustomizeRegisteredClientRepository {

    private static final String TABLE_NAME = "oauth2_registered_client";
    private static final String COLUMN_NAMES = "id, "
            + "client_id, "
            + "client_id_issued_at, "
            + "client_secret, "
            + "client_secret_expires_at, "
            + "client_name, "
            + "client_authentication_methods, "
            + "authorization_grant_types, "
            + "redirect_uris, "
            + "scopes, "
            + "client_settings,"
            + "token_settings";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM " + TABLE_NAME;
    private static final String LOAD_REGISTERED_CLIENT_SQL = "SELECT " + COLUMN_NAMES + " FROM " + TABLE_NAME;
    private static final String DELETE_REGISTERED_CLIENT_SQL = "DELETE FROM " + TABLE_NAME + " WHERE";
    private final RegisteredClientRepository registeredClientRepository;
    private final JdbcOperations jdbcOperations;

    private final RowMapper<RegisteredClient> registeredClientRowMapper = new JdbcRegisteredClientRepository.RegisteredClientRowMapper();

    public CustomizeJdbcRegisteredClientRepository(JdbcOperations jdbcOperations) {
        this(jdbcOperations, new JdbcRegisteredClientRepository(jdbcOperations));
    }

    public CustomizeJdbcRegisteredClientRepository(JdbcOperations jdbcOperations, RegisteredClientRepository registeredClientRepository) {
        this.registeredClientRepository = registeredClientRepository;
        this.jdbcOperations = jdbcOperations;
    }

    /**
     * Saves the registered client.
     *
     * @param registeredClient the {@link RegisteredClient}
     */
    @Override
    public void save(RegisteredClient registeredClient) {
        this.registeredClientRepository.save(registeredClient);
    }

    /**
     * Returns the registered client identified by the provided {@code id},
     * or {@code null} if not found.
     *
     * @param id the registration identifier
     * @return the {@link RegisteredClient} if found, otherwise {@code null}
     */
    @Override
    public RegisteredClient findById(String id) {
        return this.registeredClientRepository.findById(id);
    }

    /**
     * Returns the registered client identified by the provided {@code clientId},
     * or {@code null} if not found.
     *
     * @param clientId the client identifier
     * @return the {@link RegisteredClient} if found, otherwise {@code null}
     */
    @Override
    public RegisteredClient findByClientId(String clientId) {
        return this.registeredClientRepository.findByClientId(clientId);
    }

    @Override
    public void deleteById(String id) {
        this.deleteBy(" id = ?", id);
    }

    @Override
    public void deleteByClientId(String clientId) {
        this.deleteBy(" client_id = ?", clientId);
    }

    @Override
    public void deleteByIds(List<String> ids) {
        if (ids.size() > 1) {
            String filter = "id IN (" + "?,".repeat(Math.max(0, ids.size() - 1)) + "?)";
            this.deleteBy(filter, ids.toArray());
        } else {
            if (!ids.isEmpty()) {
                this.deleteByClientId(ids.get(0));
            }
        }
    }

    @Override
    public void deleteByClientIds(List<String> ids) {
        if (ids.size() > 1) {
            String filter = "client_id IN (" + "?,".repeat(Math.max(0, ids.size() - 1)) + "?)";
            this.deleteBy(filter, ids.toArray());
        } else {
            if (!ids.isEmpty()) {
                this.deleteByClientId(ids.get(0));
            }
        }
    }

    /**
     * find client list
     *
     * @param page page
     * @return the {@link Page<RegisteredClient>}
     */
    @Override
    public Page<RegisteredClient> findClientList(Page<?> page) {
        if (page.getPage() <= 0 || page.getSize() <= 0) {
            return Page.from(page);
        }
        final var totalRecord = this.jdbcOperations.queryForObject(COUNT_SQL, Integer.class);
        if (totalRecord == null || totalRecord == 0) {
            return Page.from(page);
        }
        var registeredClientList = this.findBy(" LIMIT ?,?", (page.getPage() - 1) * page.getSize(), page.getSize());
        final var totalPage = (totalRecord + page.getSize() - 1) / page.getSize();
        return Page.<RegisteredClient>from(page)
                   .setTotalPage(totalPage)
                   .setTotalRecord(totalRecord)
                   .setRecords(registeredClientList);
    }

    private List<RegisteredClient> findBy(String filter, Object... args) {
        return this.jdbcOperations.query(
                LOAD_REGISTERED_CLIENT_SQL + filter, this.registeredClientRowMapper, args);
    }

    private void deleteBy(String filter, Object... args) {
        this.jdbcOperations.update(DELETE_REGISTERED_CLIENT_SQL + filter, args);
    }
}
