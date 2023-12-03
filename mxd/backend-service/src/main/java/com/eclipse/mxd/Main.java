package com.eclipse.mxd;

import com.eclipse.mxd.service.ContentService;
import com.eclipse.mxd.service.Impl.ContentServiceImpl;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.logging.Logger;

public class Main {
    private final static ContentService contentService = new ContentServiceImpl();
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        contentService.getAll();
        startJettyServer();
    }

    private static void startJettyServer() {
        int port = 8080;
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/api/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "com.eclipse.mxd");
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            logger.info("exception " + e.getMessage());
        } finally {
            server.destroy();
        }
    }
}
