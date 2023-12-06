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
    private Map<String, String> settings;

    public void initialize() {
        settings = new HashMap<>();

        // Load properties from application.properties
        try (InputStream input = SettingResolver.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties appProperties = new Properties();
            appProperties.load(input);

            for (String key : appProperties.stringPropertyNames()) {
                if(key.equalsIgnoreCase("database.url")){
                    if(System.getenv("BACKEND-SERVICE-DATABASE_URL") != null){
                        settings.put(key, System.getenv("BACKEND-SERVICE-DATABASE_URL") );
                    }else{
                        settings.put(key, appProperties.getProperty(key));
                    }
                }

                if(key.equalsIgnoreCase("database.user")){
                    if(System.getenv("BACKEND-SERVICE-DATABASE_USER") != null){
                        settings.put(key, System.getenv("BACKEND-SERVICE-DATABASE_USER") );
                    }else{
                        settings.put(key, appProperties.getProperty(key));
                    }
                }

                if(key.equalsIgnoreCase("database.password")){
                    if(System.getenv("BACKEND-SERVICE-DATABASE_PASSWORD") != null){
                        settings.put(key, System.getenv("BACKEND-SERVICE-DATABASE_PASSWORD") );
                    }else{
                        settings.put(key, appProperties.getProperty(key));
                    }
                }

                if(key.equalsIgnoreCase("server.port")){
                    if(System.getenv("server-port") != null){
                        settings.put(key, System.getenv("server-port"));
                    }else{
                        settings.put(key, appProperties.getProperty(key));
                    }
                }


            }
        } catch (IOException e) {
            // Handle the exception as needed
            e.printStackTrace();
        }

        // Load environment variables
        for (Map.Entry<String, String> envVar : System.getenv().entrySet()) {
            settings.put(envVar.getKey(), envVar.getValue());
        }
    }

    public String getSetting(String key) {
        return settings.get(key);
    }
}
