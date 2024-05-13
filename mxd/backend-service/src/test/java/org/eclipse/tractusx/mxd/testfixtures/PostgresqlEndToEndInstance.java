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

package org.eclipse.tractusx.mxd.testfixtures;

import org.eclipse.edc.junit.testfixtures.TestUtils;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.sql.testfixtures.PostgresqlLocalInstance;

import java.util.HashMap;
import java.util.Map;

public interface PostgresqlEndToEndInstance {

    String USER = "postgres";
    String PASSWORD = "password";
    String JDBC_URL_PREFIX = "jdbc:postgresql://localhost:5432/";

    static void createDatabase(String dbName) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new EdcPersistenceException(e);
        }
        var postgres = new PostgresqlLocalInstance(USER, PASSWORD, JDBC_URL_PREFIX, dbName);
        postgres.createDatabase();
        try {
            var connection = postgres.getConnection(dbName);
            var sql = TestUtils
                    .getResourceFileContentAsString("schema.sql");

            try (var statement = connection.createStatement()) {
                statement.execute(sql);
            } catch (Exception exception) {
                throw new EdcPersistenceException(exception.getMessage(), exception);
            }
        } catch (Exception e) {
            throw new EdcPersistenceException(e);
        }
    }

    static Map<String, String> defaultDatasourceConfiguration(String name) {
        return new HashMap<>() {
            {
                put("edc.datasource.default.url", JDBC_URL_PREFIX + name);
                put("edc.datasource.default.user", USER);
                put("edc.datasource.default.password", PASSWORD);
            }
        };
    }

}