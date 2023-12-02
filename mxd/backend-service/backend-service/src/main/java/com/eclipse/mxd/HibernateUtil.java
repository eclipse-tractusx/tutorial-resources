package com.eclipse.mxd;


import java.io.InputStream;
import java.util.Properties;

import com.eclipse.mxd.entity.Content;
import com.eclipse.mxd.entity.Employee;
import com.eclipse.mxd.entity.Transfer;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;


public class HibernateUtil {

    private static final Logger logger = Logger.getLogger(HibernateUtil.class.getName());

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                Properties settings = new Properties();
                // Load application properties
                Properties appProperties = new Properties();
                try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
                    appProperties.load(input);
                } catch (Exception e) {
                    e.printStackTrace();
                }



                // Read database configuration from environment variables
                String databaseUrl = System.getenv("DATABASE_URL");
                String databaseUser = System.getenv("DATABASE_USER");
                String databasePassword = System.getenv("DATABASE_PASSWORD");
                // If environment variables are not set, fall back to application properties

                String databaseUrltf = System.getenv("database-connection-url");

                logger.info("database tf url : "+databaseUrltf);

                logger.info(" env databaseUrl : "+databaseUrl);
                logger.info(" env databaseUser : "+databaseUser);
                logger.info(" env databasePassword : "+databasePassword);

                logger.info(" props databaseUrl : "+appProperties.getProperty("database.url"));
                logger.info(" props databaseUser : "+appProperties.getProperty("database.user"));
                logger.info(" props databasePassword : "+appProperties.getProperty("database.password"));


                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                settings.put(Environment.URL, databaseUrltf);
                settings.put(Environment.USER,"postgres");
                settings.put(Environment.PASS, "postgres");

                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");

                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.FORMAT_SQL, "true");
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

                settings.put(Environment.HBM2DDL_AUTO, "update");
                configuration.setProperties(settings);
                configuration.addAnnotatedClass(Transfer.class);
                configuration.addAnnotatedClass(Content.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();
                logger.info("Hibernate Java Config serviceRegistry created");
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                return sessionFactory;

            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        }
        return sessionFactory;
    }
}
