package org.eclipse.mxd.database;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.eclipse.mxd.exceptions.DatabaseConnectionException;
import org.eclipse.mxd.util.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

@Singleton
@Startup
public class DatabaseConnector {
	
	private static final Logger logger = Logger.getLogger(DatabaseConnector.class.getName());

	private Connection connection;

	public DatabaseConnector() {
		try {
			initializeConnection();
		} catch (SQLException e) {
			 logger.info(e.getMessage());
			throw new DatabaseConnectionException("Failed to initialize database connection.", e);
		}
	}

	private void initializeConnection() throws SQLException {
		String url = System.getenv(Constants.BACKEND_SERVICE_HOST);
		String user = System.getenv(Constants.BACKEND_SERVICE_USERNAME);
		String password = System.getenv(Constants.BACKEND_SERVICE_PASSWORD);
		
		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			 logger.info(e.getMessage());
			throw new DatabaseConnectionException("Failed to establish database connection.", e);
		}
	}

	public Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				initializeConnection();
			}
		} catch (SQLException e) {
			 logger.info(e.getMessage());
			throw new DatabaseConnectionException("Failed to get database connection.", e);
		}
		return connection;
	}

	public void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			 logger.info(e.getMessage());
			throw new DatabaseConnectionException("Failed to close database connection.", e);
		}
	}
}
