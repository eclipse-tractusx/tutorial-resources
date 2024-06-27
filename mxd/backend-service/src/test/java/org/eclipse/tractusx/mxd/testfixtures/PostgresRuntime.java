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

import org.eclipse.edc.junit.extensions.EdcRuntimeExtension;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;

public interface PostgresRuntime {

    @RegisterExtension
    BeforeAllCallback CREATE_DATABASE = context -> PostgresqlEndToEndInstance.createDatabase(PostgresqlEndToEndInstance.DB_NAME);

    @RegisterExtension
    EdcRuntimeExtension RUNTIME = new EdcRuntimeExtension(
            "backend",
            Map.of(
                    "edc.datasource.default.url", PostgresqlEndToEndInstance.JDBC_URL_PREFIX + PostgresqlEndToEndInstance.DB_NAME,
                    "edc.datasource.default.user", PostgresqlEndToEndInstance.USER,
                    "edc.datasource.default.password", PostgresqlEndToEndInstance.PASSWORD
            )
    );

}
