package org.eclipse.mxd.database;

import javax.ejb.Stateless;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
@Stateless
public class DatabaseConnector {

    public static Connection connect() throws SQLException {
    	 Connection conn=null;
    	 
    	try
    	{
        String url = System.getenv("backend-service-host");;
        String user = System.getenv("backend-service-username");
        String password = System.getenv("backend-service-password");
        System.out.println("url "+url);
        System.out.println("user "+user);
        System.out.println("password "+password);
        System.out.println("trying to connect with these");
        conn= DriverManager.getConnection(url, user, password);
        
    	}
    	catch(Exception e)
    	{
    		
    		e.printStackTrace();
    	}
        return conn;
    }

    public  Connection getConnect() throws SQLException {
        Connection conn=null;

        try
        {
            String url = System.getenv("backend-service-host");;
            String user = System.getenv("backend-service-username");
            String password = System.getenv("backend-service-password");
            System.out.println("url "+url);
            System.out.println("user "+user);
            System.out.println("password "+password);
            System.out.println("trying to connect with these");
            conn= DriverManager.getConnection(url, user, password);

        }
        catch(Exception e)
        {

            e.printStackTrace();
        }
        return conn;
    }

}
