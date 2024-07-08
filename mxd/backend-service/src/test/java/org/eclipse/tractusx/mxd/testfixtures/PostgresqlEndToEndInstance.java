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
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

public interface PostgresqlEndToEndInstance {

    String USER = "backendservice";
    String PASSWORD = "backendservice";
    String PORT = "5432";
    String JDBC_URL_PREFIX = "jdbc:postgresql://localhost:" + PORT + "/";
    String DB_NAME = "backendservice";
    PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withExposedPorts(Integer.valueOf(PORT))
                    .withUsername(USER)
                    .withPassword(PASSWORD);

    static void createContainer() {
        postgreSQLContainer.setPortBindings(List.of(PORT + ":" + PORT));
        postgreSQLContainer.start();
    }

    static void destroyContainer() {
        if(postgreSQLContainer != null)
            postgreSQLContainer.stop();
    }

    static void createDatabase(String dbName) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new EdcPersistenceException(e);
        }
        var postgres = new PostgresqlLocalInstance(
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword(),
                postgreSQLContainer.getJdbcUrl(),
                DB_NAME);

        try {
            postgres.createDatabase();
            var connection = postgres.getConnection(dbName);
            var sql = TestUtils.getResourceFileContentAsString("schema.sql");

            try (var statement = connection.createStatement()) {
                statement.execute(sql);
            } catch (Exception exception) {

                throw new EdcPersistenceException(exception.getMessage(), exception);
            }
        } catch (Exception e) {
            throw new EdcPersistenceException(e);
        }
    }

}