package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:ucanaccess://GestionsVehicules.accdb";
            connection = DriverManager.getConnection(url);
            System.out.println("reussi");
        }
        return connection;
   	
    }
    
    public static void main (String[] args) {
    	
    }
}
