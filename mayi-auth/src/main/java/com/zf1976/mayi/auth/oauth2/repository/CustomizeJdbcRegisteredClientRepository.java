package com.zf1976.mayi.auth.oauth2.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ant
 * Create by Ant on 2021/11/19 00:34
 */
@Component
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
    public static final String COUNT = "SELECT COUNT(*) FROM " + TABLE_NAME;
    private static final String LOAD_REGISTERED_CLIENT_SQL = "SELECT " + COLUMN_NAMES + " FROM " + TABLE_NAME;
    private final RegisteredClientRepository registeredClientRepository;
    private final JdbcOperations jdbcOperations;
    private final RowMapper<RegisteredClient> registeredClientRowMapper = new JdbcRegisteredClientRepository.RegisteredClientRowMapper();

    @Autowired
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

    /**
     * find client list
     *
     * @param page page
     * @param size size
     * @return the {@link Page<List<RegisteredClient>>}
     */
    @Override
    public Page<List<RegisteredClient>> findClientList(int page, int size) {
        if (page <= 0 || size <= 0) {
            return new PageBuilder<List<RegisteredClient>>().build();
        }
        final var totalRecord = this.jdbcOperations.queryForObject(COUNT, Integer.class);
        if (totalRecord == null || totalRecord == 0) {
            return new PageBuilder<List<RegisteredClient>>().build();
        }
        final var registeredClientList = this.findBy("LIMIT ?,?", (page - 1) * size, size);
        final var totalPage = (totalRecord + page - 1) / size;
        return new PageBuilder<List<RegisteredClient>>()
                .totalRecord(totalRecord)
                .totalPage(totalPage)
                .size(size)
                .record(registeredClientList)
                .build();
    }

    private List<RegisteredClient> findBy(String filter, Object... args) {
        return this.jdbcOperations.query(
                LOAD_REGISTERED_CLIENT_SQL + filter, this.registeredClientRowMapper, args);
    }

    public static class Page<T> {

        private int totalPage;

        private int totalRecord;

        private int page;

        private int size;

        T record;

        public Page() {

        }

        public Page(PageBuilder<T> recordBuilder) {
            this.totalPage = recordBuilder.totalPage;
            this.totalRecord = recordBuilder.totalRecord;
            this.page = recordBuilder.page;
            this.size = recordBuilder.size;
            this.record = recordBuilder.t;
        }

        public int getTotalRecord() {
            return totalRecord;
        }

        public int getPage() {
            return page;
        }

        public int getSize() {
            return size;
        }

        public T getRecord() {
            return record;
        }

        public Page<T> setRecord(T record) {
            this.record = record;
            return this;
        }

        public Page<T> setTotalRecord(int totalRecord) {
            this.totalRecord = totalRecord;
            return this;
        }

        public Page<T> setPage(int page) {
            this.page = page;
            return this;
        }

        public Page<T> setSize(int size) {
            this.size = size;
            return this;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public Page<T> setTotalPage(int totalPage) {
            this.totalPage = totalPage;
            return this;
        }

        @Override
        public String toString() {
            return "Page{" +
                    "totalPage=" + totalPage +
                    ", totalRecord=" + totalRecord +
                    ", page=" + page +
                    ", size=" + size +
                    ", record=" + record +
                    '}';
        }
    }

    public static class PageBuilder<T> {

        private int totalPage;

        private int totalRecord;

        private int page;

        private int size;

        private T t;

        public PageBuilder<T> totalPage(int totalPage) {
            this.totalPage = totalPage;
            return this;
        }

        public PageBuilder<T> totalRecord(int totalRecord){
            this.totalRecord = totalRecord;
            return this;
        }

        public PageBuilder<T> page(int page) {
            this.page = page;
            return this;
        }

        public PageBuilder<T> size(int size) {
            this.size = size;
            return this;
        }

        public PageBuilder<T> record(T t) {
            this.t = t;
            return this;
        }

        public Page<T> build() {
            return new Page<>(this);
        }

    }
}
