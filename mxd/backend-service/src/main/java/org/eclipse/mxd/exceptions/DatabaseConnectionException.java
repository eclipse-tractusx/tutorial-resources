package org.eclipse.mxd.exceptions;

import java.util.logging.Logger;

public class DatabaseConnectionException extends RuntimeException {
	
	private static final Logger logger = Logger.getLogger(DatabaseConnectionException.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseConnectionException(String message) {
		super(message);
		logger.info(message);
	}

	public DatabaseConnectionException(String message, Throwable cause) {
		super(message, cause);
		logger.info(message);
	}
}
