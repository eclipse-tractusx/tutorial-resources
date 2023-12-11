
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

package org.eclipse.tractusx;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.tractusx.util.SettingResolver;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        startJettyServer();
    }

    private static void startJettyServer() {
        SettingResolver settingResolver = SettingResolver.getInstance() ;
        String port = (String) settingResolver.getSetting("server.port");
        Object ports = settingResolver.getSetting("server.port");

        Server server = new Server(Integer.parseInt(port));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/api/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "org.eclipse.tractusx");
        try {
            logger.info(server.toString());
            logger.info("server started on port : " + port);
            server.start();
            server.join();
        } catch (Exception e) {
            logger.info("server stopped");
            logger.info("exception " + e.getMessage());
        } finally {
            server.destroy();
        }
    }
}
