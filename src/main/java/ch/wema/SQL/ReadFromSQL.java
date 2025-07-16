package ch.wema.SQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

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

    public ArrayList<Integer> searchNewestActivityByUser(int autoUserId, ArrayList<Integer> list) {
        String listAsString = String.join(",", Collections.nCopies(list.size(), "?"));
        if(list.isEmpty()){
            listAsString = "\"\"";
        }
        String sql = String.format("SELECT auto_activity_id FROM Activity WHERE auto_user_id = ? AND auto_activity_id NOT IN (%s) AND endtime IS null ORDER BY starttime DESC", listAsString);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, autoUserId);
            //statement.setString(2, listAsString);
            int index = 2;
            for (Integer id : list) {
                statement.setInt(index++, id);
            }
            ResultSet rs = statement.executeQuery();
            ArrayList<Integer> activity_ids = new ArrayList<>();
            while (rs.next()) {
                activity_ids.add(rs.getInt("auto_activity_id"));
            }
            return activity_ids;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



}
