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

package org.eclipse.tractusx.mxd.backendservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.failsafe.RetryPolicy;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import org.apache.commons.dbcp2.BasicDataSource;
import org.eclipse.edc.connector.core.base.EdcHttpClientImpl;
import org.eclipse.edc.connector.core.base.OkHttpClientFactory;
import org.eclipse.edc.connector.core.base.RetryPolicyFactory;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
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

    public DataSource createLocalDataSource(ServiceExtensionContext context) {
        BasicDataSource dataSource = new BasicDataSource();
        System.out.println(context.getConfig().getEntries());
        var url = context.getConfig().getString(Constants.DATABASE_URL);
        var userName = context.getConfig().getString(Constants.DATABASE_USER);
        var password = context.getConfig().getString(Constants.DATABASE_PASSWORD);

        try {
            dataSource.setDriverClassName(Constants.DEFAULT_DRIVE);
            dataSource.setUrl(url);
            dataSource.setUsername(userName);
            dataSource.setPassword(password);
            Connection connections = dataSource.getConnection();
            InputStream inputStream = BackendServiceExtension.class.getClassLoader().getResourceAsStream(Constants.SCHEMA_PATH);
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
        }
        return dataSource;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        try {
            registerDefaultDataSource(context);
            var sqlContentStore = new SqlContentStoreServiceImpl(
                    dataSourceRegistry,
                    getDataSourceName(context),
                    transactionContext,
                    typeManager.getMapper(),
                    queryExecutor,
                    getStatementImpl(),
                    context.getMonitor());
            var sqlTransferStore = new SqlTransferStoreServiceImpl(
                    dataSourceRegistry,
                    getDataSourceName(context),
                    transactionContext,
                    typeManager.getMapper(),
                    queryExecutor,
                    getTransferStatementImpl(),
                    context.getMonitor());
            context.registerService(ContentStoreService.class, sqlContentStore);
            context.registerService(TypeTransformerRegistry.class, transformerRegistry);
            //register content controller and dependencies services here
            ContentService contentService = new ContentServiceImpl(sqlContentStore, context.getMonitor());
            context.registerService(ContentService.class, contentService);
            webService.registerResource(new ContentApiController(contentService, context.getMonitor(), getObjectMapper()));
            //register transfer controller and dependencies services here
            TransferService transferService = new TransferServiceImpl(sqlTransferStore, edcHttpClient(context), context.getMonitor(), getHttConnectionService(edcHttpClient(context), context.getMonitor()));
            context.registerService(TransferService.class, transferService);
            webService.registerResource(new TransferApiController(transferService));
        } catch (Exception e) {
            context.getMonitor().severe(e.getMessage());
            throw new EdcException(e.getMessage());
        }
    }

    private String getDataSourceName(ServiceExtensionContext context) {
        return context.getConfig().getString(Constants.DATASOURCE_NAME_SETTING, DataSourceRegistry.DEFAULT_DATASOURCE);
    }

    private ContentStatementsService getStatementImpl() {
        return statements != null ? statements : new ContentStatementsServiceImpl() {
        };
    }

    private TransferStatementsService getTransferStatementImpl() {
        return transferStatementsService != null ? transferStatementsService : new TransferStatementsServiceImpl() {
        };
    }

    public void registerDefaultDataSource(ServiceExtensionContext context) {
        dataSourceRegistry.register(Constants.DATA_SOURCE_NAME, createLocalDataSource(context));
    }

    @Provider
    public EdcHttpClient edcHttpClient(ServiceExtensionContext context) {
        return new EdcHttpClientImpl(
                okHttpClient(context),
                retryPolicy(context),
                context.getMonitor()
        );
    }

    @Provider
    public OkHttpClient okHttpClient(ServiceExtensionContext context) {
        return OkHttpClientFactory.create(context, okHttpEventListener);
    }

    @Provider
    public <T> RetryPolicy<T> retryPolicy(ServiceExtensionContext context) {
        return RetryPolicyFactory.create(context);
    }

    public int runQuery(String query, Connection connection) {
        return transactionContext.execute(() -> queryExecutor.execute(connection, query));
    }

    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    public HttpConnectionService getHttConnectionService(EdcHttpClient httpClient, Monitor monitor) {
        return new HttpConnectionService(httpClient, monitor);
    }
}
