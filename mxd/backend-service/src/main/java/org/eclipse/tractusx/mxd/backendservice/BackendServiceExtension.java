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

package org.eclipse.tractusx.mxd.backendservice;

import org.apache.commons.dbcp2.BasicDataSource;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.http.spi.EdcHttpClient;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.sql.QueryExecutor;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.tractusx.mxd.backendservice.controller.ContentApiController;
import org.eclipse.tractusx.mxd.backendservice.controller.TransferApiController;
import org.eclipse.tractusx.mxd.backendservice.service.*;
import org.eclipse.tractusx.mxd.backendservice.statements.ContentStatementsServiceImpl;
import org.eclipse.tractusx.mxd.backendservice.statements.TransferStatementsServiceImpl;
import org.eclipse.tractusx.mxd.backendservice.store.SqlContentStoreServiceImpl;
import org.eclipse.tractusx.mxd.backendservice.store.SqlTransferStoreServiceImpl;
import org.eclipse.tractusx.mxd.util.Constants;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

@Extension(BackendServiceExtension.NAME)
public class BackendServiceExtension implements ServiceExtension {

    public static final String NAME = "Backend Services";

    @Inject
    private WebService webService;

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Inject
    private TransactionContext transactionContext;

    @Inject
    private TypeManager typeManager;

    @Inject
    private QueryExecutor queryExecutor;

    @Inject
    private EdcHttpClient edcHttpClient;

    @Setting(value = "host name")
    private final static String HOSTNAME_SETTING = "edc.hostname";
    @Setting(value = "host name")
    private final static String PORT_SETTING = "web.http.port";

    private final static String DEFAULT_HOST = "localhost";
    private final static String DEFAULT_PORT = "8080";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        dataSourceRegistry.register(Constants.DATA_SOURCE_NAME, createLocalDataSource(context));

        Monitor monitor = context.getMonitor();
        String dataSourceName = context.getConfig().getString(Constants.DATASOURCE_NAME_SETTING,
                DataSourceRegistry.DEFAULT_DATASOURCE);

        // Initialize SQL content store
        var sqlContentStore = new SqlContentStoreServiceImpl(
                dataSourceRegistry,
                dataSourceName,
                transactionContext,
                typeManager.getMapper(),
                queryExecutor,
                new ContentStatementsServiceImpl(),
                monitor);
        ContentService contentService = new ContentServiceImpl(sqlContentStore, monitor);
        webService.registerResource(
                new ContentApiController(
                        contentService,
                        monitor,
                        typeManager.getMapper(),
                        context.getSetting(HOSTNAME_SETTING, DEFAULT_HOST),
                        context.getSetting(PORT_SETTING, DEFAULT_PORT)
                ));

        // Initialize SQL transfer store
        var sqlTransferStore = new SqlTransferStoreServiceImpl(
                dataSourceRegistry,
                dataSourceName,
                transactionContext,
                typeManager.getMapper(),
                queryExecutor,
                new TransferStatementsServiceImpl(),
                monitor);

        TransferService transferService = new TransferServiceImpl(
                sqlTransferStore,
                edcHttpClient,
                monitor,
                new HttpConnectionService(edcHttpClient, monitor));
        webService.registerResource(
                new TransferApiController(
                        transferService,
                        monitor,
                        typeManager.getMapper()
                ));
    }

    public DataSource createLocalDataSource(ServiceExtensionContext context) {
        BasicDataSource dataSource = new BasicDataSource();

        var url = context.getConfig().getString(Constants.DATABASE_URL);
        var userName = context.getConfig().getString(Constants.DATABASE_USER);
        var password = context.getConfig().getString(Constants.DATABASE_PASSWORD);

        try {
            dataSource.setDriverClassName(Constants.DEFAULT_DRIVE);
            dataSource.setUrl(url);
            dataSource.setUsername(userName);
            dataSource.setPassword(password);
            Connection connections = dataSource.getConnection();
            InputStream inputStream = BackendServiceExtension.class
                    .getClassLoader()
                    .getResourceAsStream(Constants.SCHEMA_PATH);
            if (inputStream != null) {
                String schema = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                transactionContext.execute(() -> queryExecutor.execute(connections, schema));
                context.getMonitor().info(String.valueOf(connections));
            } else {
                context.getMonitor().info("schema.sql File not found on the classpath.");
            }
            return dataSource;
        } catch (Exception e) {
            context.getMonitor().severe(e.getMessage());
            throw new EdcException(e.getMessage());
        }
    }
}
