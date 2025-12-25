package ch.wema.SQL;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseService.class);
    private static final HikariDataSource dataSource;
    private static final int MAX_POOL_SIZE = 10;
    private static final int WARNING_THRESHOLD = 8;

    static {
        String dbHost = System.getenv("DB_HOST");
        String dbUsername = System.getenv("DB_USERNAME");
        String dbPassword = System.getenv("DB_PASSWORD");
        String dbName = "sugu";
        int dbPort = 3306;

        HikariConfig config = new HikariConfig();
        config.setPoolName("SuguPool");
        config.setJdbcUrl(String.format("jdbc:mariadb://%s:%d/%s", dbHost, dbPort, dbName));
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(MAX_POOL_SIZE);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        HikariPoolMXBean poolStats = dataSource.getHikariPoolMXBean();
        int activeConnections = poolStats.getActiveConnections();
        int idleConnections = poolStats.getIdleConnections();
        int waitingThreads = poolStats.getThreadsAwaitingConnection();

        if (activeConnections >= WARNING_THRESHOLD) {
            LOGGER.warn("Connection pool running low! Active: {}/{}, Idle: {}, Waiting: {} - Consider increasing pool size",
                    activeConnections, MAX_POOL_SIZE, idleConnections, waitingThreads);
        }

        if (waitingThreads > 0) {
            LOGGER.warn("Threads waiting for connection: {} - Pool may be exhausted", waitingThreads);
        }

        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
