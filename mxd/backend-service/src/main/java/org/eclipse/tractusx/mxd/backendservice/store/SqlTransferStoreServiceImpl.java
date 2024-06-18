/********************************************************************************
 *  Copyright (c) 2024 SAP SE
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       SAP SE - initial API and implementation
 *
 ********************************************************************************/

package org.eclipse.tractusx.mxd.backendservice.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.spi.EdcException;
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

    private final TransferStatementsService statements;
    private final Monitor monitor;

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
    public void save(Transfer transfer, String content) {
        try (var connection = getConnection()) {
            insertInternal(connection, transfer, content);
        } catch (Exception e) {
            throw new EdcPersistenceException(e.getMessage(), e);
        }
    }

    private TransferResponse mapResultSet(ResultSet resultSet) throws Exception {
        return TransferResponse.builder()
                .asset(resultSet.getString(statements.getAssetColumn()))
                .contents(resultSet.getString(statements.getContentsColumn()))
                .transferID(resultSet.getString(statements.getTransferIdColumn()))
                .createdDate(resultSet.getDate(statements.getCreatedDateColumn()))
                .updatedDate(resultSet.getDate(statements.getUpdatedDateColumn()))
                .build();
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
            throw new EdcException("Error creating transfer" + e.getMessage());
        }
    }
}
