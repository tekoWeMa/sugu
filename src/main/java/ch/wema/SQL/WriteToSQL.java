package ch.wema.SQL;

import java.sql.*;

public class WriteToSQL {

    private Connection connection;

    public WriteToSQL(Connection DBConnection) {
        this.connection = DBConnection;
    }

    public int insertUser(String username, long userId) throws SQLException {
        String sql = "INSERT INTO User (username, user_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setLong(2, userId);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Assuming the auto-generated key is an integer.
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    public int insertApplication(String name, long appId) throws SQLException {
        String sql = "INSERT INTO Application (name, app_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setLong(2, appId);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Assuming the auto-generated key is an integer.
                } else {
                    throw new SQLException("Creating Application failed, no ID obtained.");
                }
            }
        }
    }

    public int insertApplicationNameOnly(String name) throws SQLException {
        String sql = "INSERT INTO Application (name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Assuming the auto-generated key is an integer.
                } else {
                    throw new SQLException("Creating Application failed, no ID obtained.");
                }
            }
        }
    }

    public int insertStatus(String type) throws SQLException {
        String sql = "INSERT INTO Status (type) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, type);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Assuming the auto-generated key is an integer.
                } else {
                    throw new SQLException("Creating Status failed, no ID obtained.");
                }
            }
        }
    }

    public int insertAppState(String details, String state) throws SQLException {
        String sql = "INSERT INTO AppState (details, state) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, details);
            statement.setString(2, state);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Assuming the auto-generated key is an integer.
                } else {
                    throw new SQLException("Creating AppState failed, no ID obtained.");
                }
            }
        }
    }

    public int insertType(String type) throws SQLException {
        String sql = "INSERT INTO Type (type) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, type);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Assuming the auto-generated key is an integer.
                } else {
                    throw new SQLException("Creating Type failed, no ID obtained.");
                }
            }
        }
    }

    public int insertActivity(int autoAppId, int autoUserId, int autoStatusId, int autoAppStateId, int autoTypeId, Timestamp starttime) throws SQLException {
        String sql = "INSERT INTO Activity (auto_app_id, auto_user_id, auto_status_id, auto_app_state_id, auto_type_id, starttime) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, autoAppId);
            statement.setInt(2, autoUserId);
            statement.setInt(3, autoStatusId);
            statement.setInt(4, autoAppStateId);
            statement.setInt(5, autoTypeId);
            statement.setTimestamp(6, starttime);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Assuming the auto-generated key is an integer.
                } else {
                    throw new SQLException("Creating Activity failed, no ID obtained.");
                }
            }
        }
    }

    public int insertActivitySlim(int autoUserId, int autoStatusId, Timestamp starttime) throws SQLException {
        String sql = "INSERT INTO Activity (auto_user_id, auto_status_id, starttime) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, autoUserId);
            statement.setInt(2, autoStatusId);
            statement.setTimestamp(3, starttime);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Assuming the auto-generated key is an integer.
                } else {
                    throw new SQLException("Creating Activity failed, no ID obtained.");
                }
            }
        }
    }

    public void updateEndActivity(int autoActivityId, Timestamp endtime) throws SQLException {
        String sql = "UPDATE Activity SET endtime = ? WHERE auto_activity_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, endtime);
            statement.setInt(2, autoActivityId);
            statement.executeUpdate();
        }
    }

    public void deleteEmptyEndDates() throws SQLException {
        String sql = "DELETE FROM Activity WHERE endtime IS NULL;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }


    // Additional methods for updating or deleting records can be added here

}

