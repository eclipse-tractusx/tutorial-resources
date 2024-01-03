/*******************************************************************************
 *
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 ******************************************************************************/

package org.eclipse.tractusx.mxd.backendservice.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.result.StoreResult;
import org.eclipse.edc.sql.QueryExecutor;
import org.eclipse.edc.sql.store.AbstractSqlStore;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.eclipse.tractusx.mxd.backendservice.entity.Content;
import org.eclipse.tractusx.mxd.backendservice.entity.ContentResponse;
import org.eclipse.tractusx.mxd.backendservice.statements.ContentStatementsService;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static java.lang.String.format;

public class SqlContentStoreServiceImpl extends AbstractSqlStore implements ContentStoreService {

    private ContentStatementsService statements;
    private Monitor monitor;

    public SqlContentStoreServiceImpl(DataSourceRegistry dataSourceRegistry,
                                      String dataSourceName,
                                      TransactionContext transactionContext,
                                      ObjectMapper objectMapper,
                                      QueryExecutor queryExecutor,
                                      ContentStatementsService statements,
                                      Monitor monitor) {
        super(dataSourceRegistry, dataSourceName, transactionContext, objectMapper, queryExecutor);
        this.statements = statements;
        this.monitor = monitor;
    }

    @Override
    public @NotNull Stream<ContentResponse> findAll(QuerySpec spec) {
        return transactionContext.execute(() -> {
            Objects.requireNonNull(statements.createQuery(spec));
            try {
                var queryStmt = statements.createQuery(spec);
                return queryExecutor.query(getConnection(), true, this::mapResultSet, queryStmt.getQueryAsString(), queryStmt.getParameters());
            } catch (SQLException exception) {
                monitor.warning(exception.getMessage());
                throw new EdcPersistenceException(exception);
            }
        });
    }

    @Override
    public StoreResult<ContentResponse> findById(String contentId) {
        Objects.requireNonNull(contentId);
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                var content = findContentById(connection, contentId);
                if (content == null) {
                    return StoreResult.notFound(format(CONTENT_NOT_FOUND_TEMPLATE, contentId));
                }
                return StoreResult.success(content);
            } catch (Exception exception) {
                monitor.warning(exception.getMessage());
                throw new EdcPersistenceException(exception.getMessage(), exception);
            }
        });
    }

    @Override
    public String save(Object content) {
        String urlResponse = "";
        try (var connection = getConnection()) {
            urlResponse = insertInternal(connection, content).toString();

        } catch (Exception exception) {
            monitor.warning(exception.getMessage());
            throw new EdcPersistenceException(exception.getMessage(), exception);
        }
        return urlResponse;
    }

    private UUID insertInternal(Connection connection, Object content) {
        try {
            monitor.info(toJson(content));
            UUID uuid = UUID.randomUUID();
            int affectedRow = queryExecutor.execute(connection, statements.getInsertTemplate(), uuid, toJson(content), new Date(), new Date());
            if (affectedRow > 0) {
                return uuid;
            }
        } catch (Exception exception) {
            monitor.warning(exception.getMessage());
            throw new EdcPersistenceException(exception.getMessage(), exception);
        }
        return null;
    }

    private ContentResponse mapResultSet(ResultSet resultSet) throws Exception {

        ContentResponse content = new ContentResponse.Builder()
                .setId(resultSet.getString(statements.getIdColumn()))
                .setData(resultSet.getString(statements.getAssetColumn()))
                .build();
        return content;
    }

    private ContentResponse findContentById(Connection connection, String id) {
        var sql = statements.getFindByTemplate();
        return queryExecutor.single(connection, false, this::mapResultSet, sql, id);
    }

    @Override
    public StoreResult<Void> update(Content definition) {
        return null;
    }

    @Override
    public StoreResult<Content> deleteById(String id) {
        return null;
    }
}
