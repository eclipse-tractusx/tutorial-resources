/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.tractusx.mxd.dataservice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.eclipse.edc.runtime.metamodel.annotation.Configuration;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.runtime.metamodel.annotation.Settings;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.web.spi.WebServer;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.PortMapping;
import org.eclipse.edc.web.spi.configuration.PortMappingRegistry;
import org.eclipse.tractusx.mxd.dataservice.api.DataServiceApiController;
import org.eclipse.tractusx.mxd.dataservice.model.DataRecord;

@Extension(DataServiceExtension.NAME)
public class DataServiceExtension implements ServiceExtension {

    public static final String NAME = "MXD Demo Backend Services";
    public static final String DATA_API_CONTEXT_NAME = "data";
    @Inject
    private WebService webService;
    @Inject
    private WebServer webServer;

    @Configuration
    private DataServiceApiConfiguration apiConfig;
    @Inject
    private PortMappingRegistry portMappingRegistry;
    @Inject
    private TypeManager typeManager;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {

        portMappingRegistry.register(new PortMapping(DATA_API_CONTEXT_NAME, apiConfig.port, apiConfig.path));

        var database = new ConcurrentHashMap<String, DataRecord>();
        populate(database);
        webService.registerResource(DATA_API_CONTEXT_NAME, new DataServiceApiController(database, typeManager.getMapper()));
    }

    private void populate(Map<String, DataRecord> database) {
        IntStream.range(0, 10)
                .mapToObj(i -> new DataRecord("id" + i, "Record Nr. " + i, "This is a record (nr. " + i + ") coming from the provider's private HTTP data service"))
                .forEach(dr -> database.put(dr.id(), dr));
    }

    @Settings
    record DataServiceApiConfiguration(
            @Setting(key = "web.http." + DATA_API_CONTEXT_NAME + ".port", defaultValue = "8080")
            int port,
            @Setting(key = "web.http." + DATA_API_CONTEXT_NAME + ".path", defaultValue = "/")
            String path
    ) {
    }

}
