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

import org.eclipse.edc.runtime.metamodel.annotation.Configuration;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.runtime.metamodel.annotation.SettingContext;
import org.eclipse.edc.runtime.metamodel.annotation.Settings;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.PortMapping;
import org.eclipse.edc.web.spi.configuration.PortMappingRegistry;
import org.eclipse.tractusx.mxd.dataservice.api.DataServiceApiController;
import org.eclipse.tractusx.mxd.dataservice.model.DataRecord;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Extension(DataServiceExtension.NAME)
public class DataServiceExtension implements ServiceExtension {

    public static final String NAME = "MXD Demo Backend Services";
    private static final String DEFAULT_PATH = "/";
    private static final String DATA_API_CONTEXT_NAME = "data";
    private final static int DEFAULT_PORT = 8080;
    @SettingContext("Version API context setting key")
    private static final String DATA_API_CONFIG_KEY = "web.http.data";
    @Inject
    private WebService webService;
    @Inject
    private PortMappingRegistry portMappingRegistry;
    @Configuration
    private DataServiceExtensionConfiguration apiConfiguration;

    @Settings
    record DataServiceExtensionConfiguration(
            @Setting(key = DATA_API_CONFIG_KEY + ".port", description = "Port for " + DATA_API_CONTEXT_NAME + " api context", defaultValue = DEFAULT_PORT + "")
            int port,
            @Setting(key = DATA_API_CONFIG_KEY + ".path", description = "Path for " + DATA_API_CONTEXT_NAME + " api context", defaultValue = DEFAULT_PATH)
            String path
    ) {

    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var portMapping = new PortMapping(DATA_API_CONTEXT_NAME, apiConfiguration.port(), apiConfiguration.path());
        portMappingRegistry.register(portMapping);
        var database = new ConcurrentHashMap<String, DataRecord>();
        populate(database);
        webService.registerResource(DATA_API_CONTEXT_NAME, new DataServiceApiController(database));
    }

    private void populate(Map<String, DataRecord> database) {
        IntStream.range(0, 10)
                .mapToObj(i -> new DataRecord("id" + i, "Record Nr. " + i, "This is a record (nr. " + i + ") coming from the provider's private HTTP data service"))
                .forEach(dr -> database.put(dr.id(), dr));
    }

}
