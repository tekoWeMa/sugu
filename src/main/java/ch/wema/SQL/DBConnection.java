package ch.wema.SQL;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public java.sql.Connection SQLDBConnection() {
        final var dbHost = System.getenv("DB_HOST"); // DB Host, e.g., "localhost" or an IP address
        final var dbUsername = System.getenv("DB_USERNAME"); // DB Username
        final var dbPassword = System.getenv("DB_PASSWORD"); // DB Password

        String dbName = "sugu";
        int dbPort = 3306; // Default MariaDB port
        // Construct the connection URL
        String connectionUrl = String.format("jdbc:mariadb://%s:%d/%s", dbHost, dbPort, dbName);

        try {
            // Establish and return the database connection
            return DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
