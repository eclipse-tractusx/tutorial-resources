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

package com.eclipse.mxd.util;


import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import com.eclipse.mxd.entity.Content;
import com.eclipse.mxd.entity.Transfer;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static final Logger logger = Logger.getLogger(HibernateUtil.class.getName());

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                Properties settings = new Properties();
                Properties appProperties = new Properties();
                try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
                    appProperties.load(input);
                } catch (Exception e) {
                    logger.info(" error " + e.getMessage());
                }
                // Read database configuration from environment variables
                String databaseUrl = System.getenv("BACKEND-SERVICE-DATABASE_URL");
                String databaseUser = System.getenv("BACKEND-SERVICE-DATABASE_USER");
                String databasePassword = System.getenv("BACKEND-SERVICE-DATABASE_PASSWORD");
                // If environment variables are not set, fall back to application properties
                logger.info(" env databaseUrl : " + databaseUrl);
                logger.info(" env databaseUser : " + databaseUser);
                logger.info(" env databasePassword : " + databasePassword);
                logger.info(" props databaseUrl : " + appProperties.getProperty("database.url"));
                logger.info(" props databaseUser : " + appProperties.getProperty("database.user"));
                logger.info(" props databasePassword : " + appProperties.getProperty("database.password"));
                if ((databaseUrl != null && !databaseUrl.isEmpty()) && (databaseUser != null && !databaseUser.isEmpty()) && (databasePassword != null && !databasePassword.isEmpty())) {
                    settings.put(Environment.URL, databaseUrl);
                    settings.put(Environment.USER, databaseUser);
                    settings.put(Environment.PASS, databasePassword);
                    logger.info(" connecting using evn variables");
                } else {
                    settings.put(Environment.URL, appProperties.getProperty("database.url"));
                    settings.put(Environment.USER, appProperties.getProperty("database.user"));
                    settings.put(Environment.PASS, appProperties.getProperty("database.password"));
                    logger.info(" connecting using application.properties variables");
                }
                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.FORMAT_SQL, "true");
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                settings.put(Environment.HBM2DDL_AUTO, "create-drop");
                configuration.setProperties(settings);
                configuration.addAnnotatedClass(Transfer.class);
                configuration.addAnnotatedClass(Content.class);
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();
                logger.info("Hibernate Java Config serviceRegistry created");
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                return sessionFactory;
            } catch (Exception e) {
                logger.info(" error " + e.getMessage());
            }
        }
        return sessionFactory;
    }
}
