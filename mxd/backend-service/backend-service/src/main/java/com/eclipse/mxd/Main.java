package com.eclipse.mxd;

import com.eclipse.mxd.entity.Employee;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.glassfish.jersey.servlet.ServletContainer;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Arrays;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // start a transaction

            transaction = session.beginTransaction();
            // save the student object
            session.save(new Employee(1,"hello"));
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.info(e.getMessage())
        }
        startJettyServer();

    }

    private static void startJettyServer() {
        int port = 8080;

        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        server.setHandler(context);

        // Map Jersey to the root path
        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/api/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "com.eclipse.mxd");
      ServletMapping[] ss= context.getServletHandler().getServletMappings();
        // Print servlet mappings
       for(ServletMapping servletMapping:ss) {
           logger.info("line no 58 " + servletMapping.getServletName());
           String[] aa = servletMapping.getPathSpecs();
           for (String str : aa) {
               logger.info("line no 60" + str);
           }
       }
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }

}
