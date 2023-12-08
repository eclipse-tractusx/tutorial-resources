/*******************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *    Contributors:Ravinder Kumar
 *    Backend-API and implementation
 *
 ******************************************************************************/

package org.eclipse.mxd.util;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SettingResolver {
    private static final SettingResolver instance = new SettingResolver();
    private final Map<Object, Object> settings = new HashMap<>();

    private SettingResolver() {
        initialize();
    }

    public static SettingResolver getInstance() {
        return instance;
    }

    public void initialize() {
        try (InputStream input = SettingResolver.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties appProperties = new Properties();
            appProperties.load(input);
            if(System.getenv("server.port") != null
            && System.getenv("database.url") != null
            && System.getenv("database.user") != null
            && System.getenv("database.password") != null
            ){
                this.settings.putAll(System.getenv());
            }else{
                this.settings.putAll(appProperties);
            }
        } catch (IOException e) {

        }
    }

    public Object getSetting(String key) {
        return this.settings.get(key);
    }

    public Map<Object,Object> getAllSetting() {
        return this.settings;
    }
}
