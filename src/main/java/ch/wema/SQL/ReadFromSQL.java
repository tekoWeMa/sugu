package ch.wema.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReadFromSQL {

    private Connection connection;

    public ReadFromSQL(Connection connection) {
        this.connection = connection;
    }

    public Integer searchUser(long userId) {
        String sql = "SELECT auto_user_id FROM User WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("auto_user_id");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Integer searchApplicationById(long appId) {
        String sql = "SELECT auto_app_id FROM Application WHERE app_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, appId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("auto_app_id");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer searchApplicationByName(String appname) {
        String sql = "SELECT auto_app_id FROM Application WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, appname);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("auto_app_id");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer searchStatus(String type) {
        String sql = "SELECT auto_status_id FROM Status WHERE type = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, type);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("auto_status_id");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer searchAppState(String state, String details) {
        String sql = "SELECT auto_app_state_id FROM AppState WHERE state = ? AND details = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, state);
            statement.setString(2, details);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("auto_app_state_id");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer searchType(String type) {
        String sql = "SELECT auto_type_id FROM Type WHERE type = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, type);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("auto_type_id");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer searchNewestActivityByUser(int autoUserId) {
        String sql = "SELECT auto_activity_id FROM Activity WHERE auto_user_id = ? ORDER BY starttime DESC LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, autoUserId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("auto_activity_id");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



}
