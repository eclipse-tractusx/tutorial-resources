/*******************************************************************************
 *
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
import org.eclipse.tractusx.mxd.backendservice.entity.Transfer;
import org.eclipse.tractusx.mxd.backendservice.entity.TransferResponse;
import org.eclipse.tractusx.mxd.backendservice.statements.TransferStatementsService;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.String.format;

public class SqlTransferStoreServiceImpl extends AbstractSqlStore implements TransferStoreService {

    private TransferStatementsService statements;
    private Monitor monitor;

    public SqlTransferStoreServiceImpl(DataSourceRegistry dataSourceRegistry,
                                       String dataSourceName,
                                       TransactionContext transactionContext,
                                       ObjectMapper objectMapper,
                                       QueryExecutor queryExecutor,
                                       TransferStatementsService statements,
                                       Monitor monitor) {
        super(dataSourceRegistry, dataSourceName, transactionContext, objectMapper, queryExecutor);
        this.statements = statements;
        this.monitor = monitor;
    }

    @Override
    public @NotNull Stream<TransferResponse> findAll(QuerySpec spec) {
        return transactionContext.execute(() -> {
            Objects.requireNonNull(statements.createQuery(spec));
            try {
                var queryStmt = statements.createQuery(spec);
                return queryExecutor.query(getConnection(), true, this::mapResultSet, queryStmt.getQueryAsString(), queryStmt.getParameters());
            } catch (SQLException exception) {
                throw new EdcPersistenceException(exception);
            }
        });
    }

    @Override
    public StoreResult<TransferResponse> findById(String transferId) {
        Objects.requireNonNull(transferId);
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                var content = findById(connection, transferId);
                if (content == null) {
                    return StoreResult.notFound(format(CONTRACT_NOT_FOUND, transferId));
                }
                return StoreResult.success(content);
            } catch (Exception exception) {
                return StoreResult.notFound(format(exception.getMessage(), exception));
            }
        });
    }

    @Override
    public StoreResult<Void> save(Transfer transfer, String content) {
        try (var connection = getConnection()) {
            insertInternal(connection, transfer, content);
        } catch (Exception e) {
            throw new EdcPersistenceException(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public StoreResult<Void> update(Transfer transfer) {
        return null;
    }

    @Override
    public StoreResult<Transfer> deleteById(String id) {
        return null;
    }

    private TransferResponse mapResultSet(ResultSet resultSet) throws Exception {
        TransferResponse response = TransferResponse.builder()
                .asset(resultSet.getString(statements.getAssetColumn()))
                .contents(resultSet.getString(statements.getContentsColumn()))
                .transferID(resultSet.getString(statements.getTransferIdColumn()))
                .createdDate(resultSet.getDate(statements.getCreatedDateColumn()))
                .updatedDate(resultSet.getDate(statements.getUpdatedDateColumn()))
                .build();

        return response;
    }

    private TransferResponse findById(Connection connection, String id) {
        var sql = statements.getFindByTemplate();
        return queryExecutor.single(connection, false, this::mapResultSet, sql, id);
    }

    private void insertInternal(Connection connection, Transfer transfer, String content) {
        try {
            transactionContext.execute(() -> {
                queryExecutor.execute(connection, statements.getInsertTemplate(), transfer.getId(), toJson(transfer), toJson(content), new Date(), new Date());
            });
        } catch (Exception e) {
            monitor.warning(e.getMessage(), e);
        }
    }
}
