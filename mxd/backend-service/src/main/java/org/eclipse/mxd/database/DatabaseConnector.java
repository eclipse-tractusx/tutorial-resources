package org.eclipse.mxd.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    public static Connection connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/test";
        String user = "postgres";
        String password = "root";
        return DriverManager.getConnection(url, user, password);
    }

}
