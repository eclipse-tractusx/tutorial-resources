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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.failsafe.RetryPolicy;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import org.apache.commons.dbcp2.BasicDataSource;
import org.eclipse.edc.connector.core.base.OkHttpClientConfiguration;
import org.eclipse.edc.connector.core.base.OkHttpClientFactory;
import org.eclipse.edc.connector.core.base.RetryPolicyConfiguration;
import org.eclipse.edc.connector.core.base.RetryPolicyFactory;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.http.EdcHttpClient;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.sql.QueryExecutor;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.http.client.EdcHttpClientImpl;
import org.eclipse.tractusx.mxd.backendservice.controller.ContentApiController;
import org.eclipse.tractusx.mxd.backendservice.controller.TransferApiController;
import org.eclipse.tractusx.mxd.backendservice.service.*;
import org.eclipse.tractusx.mxd.backendservice.statements.ContentStatementsService;
import org.eclipse.tractusx.mxd.backendservice.statements.ContentStatementsServiceImpl;
import org.eclipse.tractusx.mxd.backendservice.statements.TransferStatementsService;
import org.eclipse.tractusx.mxd.backendservice.statements.TransferStatementsServiceImpl;
import org.eclipse.tractusx.mxd.backendservice.store.ContentStoreService;
import org.eclipse.tractusx.mxd.backendservice.store.SqlContentStoreServiceImpl;
import org.eclipse.tractusx.mxd.backendservice.store.SqlTransferStoreServiceImpl;
import org.eclipse.tractusx.mxd.backendservice.store.TransferStoreService;
import org.eclipse.tractusx.mxd.util.Constants;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

@Extension(BackendServiceExtension.NAME)
public class BackendServiceExtension implements ServiceExtension {

    public static final String NAME = "Backend Services";

    @Inject(required = false)
    private EventListener okHttpEventListener;

    @Inject
    private WebService webService;

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Inject
    private TransactionContext transactionContext;

    @Inject
    private TypeManager typeManager;

    @Inject(required = false)
    private ContentStatementsService statements;

    @Inject(required = false)
    private TransferStatementsService transferStatementsService;

    @Inject
    private TypeTransformerRegistry transformerRegistry;

    @Inject
    private QueryExecutor queryExecutor;

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
        try {
            registerDefaultDataSource(context);

            Monitor monitor = context.getMonitor();
            String dataSourceName = getDataSourceName(context);
            EdcHttpClient edcHttpClient = createEdcHttpClient(context);

            // Initialize SQL content store
            var sqlContentStore = new SqlContentStoreServiceImpl(
                    dataSourceRegistry,
                    dataSourceName,
                    transactionContext,
                    typeManager.getMapper(),
                    queryExecutor,
                    getStatementImpl(),
                    monitor);
            //register content service
            context.registerService(ContentStoreService.class, sqlContentStore);
            ContentService contentService = new ContentServiceImpl(sqlContentStore, monitor);
            context.registerService(ContentService.class, contentService);
            webService.registerResource(
                    new ContentApiController(
                            contentService,
                            monitor,
                            getObjectMapper(),
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
                    getTransferStatementImpl(),
                    monitor);

            //register transfer service
            context.registerService(TransferStoreService.class, sqlTransferStore);
            TransferService transferService = new TransferServiceImpl(
                    sqlTransferStore,
                    edcHttpClient,
                    monitor,
                    getHttConnectionService(edcHttpClient, monitor));
            context.registerService(TransferService.class, transferService);
            webService.registerResource(
                    new TransferApiController(
                            transferService,
                            monitor,
                            getObjectMapper()
                    ));
        } catch (Exception e) {
            context.getMonitor().severe(e.getMessage());
            throw new EdcException(e.getMessage());
        }
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
                runQuery(schema, connections);
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

    private String getDataSourceName(ServiceExtensionContext context) {
        return context.getConfig().getString(Constants.DATASOURCE_NAME_SETTING,
                DataSourceRegistry.DEFAULT_DATASOURCE);
    }

    private ContentStatementsService getStatementImpl() {
        return statements != null ? statements : new ContentStatementsServiceImpl() {
        };
    }

    private TransferStatementsService getTransferStatementImpl() {
        return transferStatementsService != null ? transferStatementsService :
                new TransferStatementsServiceImpl() {
                };
    }

    public void registerDefaultDataSource(ServiceExtensionContext context) {
        dataSourceRegistry.register(Constants.DATA_SOURCE_NAME, createLocalDataSource(context));
    }

    @Provider
    public EdcHttpClient createEdcHttpClient(ServiceExtensionContext context) {
        return new EdcHttpClientImpl(
                createOkHttpClient(context),
                createRetryPolicy(context),
                context.getMonitor()
        );
    }

    @Provider
    public OkHttpClient createOkHttpClient(ServiceExtensionContext context) {
        return OkHttpClientFactory.create(OkHttpClientConfiguration.Builder.newInstance().build(), okHttpEventListener, context.getMonitor());
    }

    @Provider
    public <T> RetryPolicy<T> createRetryPolicy(ServiceExtensionContext context) {
        var configuration = RetryPolicyConfiguration.Builder.newInstance()
                .logOnAbort(true)
                .logOnRetryScheduled(true)
                .logOnRetry(true)
                .logOnRetriesExceeded(true)
                .logOnFailedAttempt(true)
                .build();
        return (RetryPolicy<T>) RetryPolicyFactory.create(configuration, context.getMonitor());
    }

    public int runQuery(String query, Connection connection) {
        return transactionContext.execute(() -> queryExecutor.execute(connection, query));
    }

    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    public HttpConnectionService getHttConnectionService(EdcHttpClient httpClient, Monitor monitor) {
        return new HttpConnectionService(httpClient, monitor);
    }
}
