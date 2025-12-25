package ch.wema.reactions;

import ch.wema.SQL.DBConnection;
import ch.wema.SQL.ReadFromSQL;
import ch.wema.SQL.WriteToSQL;
import ch.wema.Sugu;
import ch.wema.core.reaction.Reaction;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import java.sql.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ActivityReaction implements Reaction<PresenceUpdateEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sugu.class);

    @Override
    public Mono<Void> handle(PresenceUpdateEvent event) {
        //check if this is even getting used
        return Mono.just(event)
                                .flatMap(e -> {
                                    String userId = e.getUserId().asString();
                                    var client = e.getClient();
                                    var user = client.getUserById(Snowflake.of(userId));
                                    return user.flatMap(u -> {
                                        // Extract the user's status from their status
                                        String status = event.getCurrent().getStatus().toString();

                        // Build the base log message
                        StringBuilder content = new StringBuilder();
                        content.append("The Status of the user ")
                               .append(u.getUsername())
                               .append(" (").append(u.getId().asString()).append(") ")
                               .append("changed to ").append(status).append(".");

                        //Make Connection to DDB
                        DBConnection dbConnection = new DBConnection();
                        Connection conn = dbConnection.SQLDBConnection();
                        ReadFromSQL readFromSQL = new ReadFromSQL(conn);
                        WriteToSQL writeToSQL = new WriteToSQL(conn);
                        //Get Current Time
                        java.util.Date date = new java.util.Date();
                        Timestamp currenttime = new Timestamp(date.getTime());
                        //Variables for User
                        long userID = u.getId().asLong();
                        String username = u.getUsername();
                        //Look up, if User exists in DB
                        Integer autouserid = readFromSQL.searchUser(userID);
                        //Insert User, if User does not exist.
                        if (autouserid == null){
                            try {
                                autouserid = writeToSQL.insertUser(username, userID);
                            } catch (SQLException ex) {
                                return Mono.error(new RuntimeException(ex));
                            }
                        }

                        //Variables for Statustype
                                        //Look up, if Status exists in DB
                        Integer autostatusid = readFromSQL.searchStatus(status);
                        //Insert Status, if Status does not exist.
                        if (autostatusid == null) {
                            try {
                                autostatusid = writeToSQL.insertStatus(status);
                            } catch (SQLException ex) {
                                return Mono.error(new RuntimeException(ex));
                            }
                        }

                        ArrayList<Integer> activityIds = new ArrayList<>();
                        // If the user has any activities, append them to the log message
                        List<Activity> activities = event.getCurrent().getActivities();
                        if (!activities.isEmpty()) {
                            content.append("\nActivities:");
                            for (Activity activity : activities) {
                                /* 
                                If 
                                Name: Custom Status or
                                Type: CUSTOM
                                We can ignore the activity only user status information
                                */

                                if (activity.getName().equals("Custom Status") || activity.getType().name().equals("CUSTOM")){
                                    if (activities.size() == 1) {
                                        try {
                                            updatePrevActivity(readFromSQL, writeToSQL, autouserid, currenttime, activityIds);
                                        } catch (SQLException ex) {
                                            return Mono.error(new RuntimeException(ex));
                                        }
                                        try {
                                            writeToSQL.insertActivitySlim(autouserid, autostatusid, currenttime);
                                        } catch (SQLException ex) {
                                            return Mono.error(new RuntimeException(ex));
                                        }
                                    }
                                    continue;
                                }

                                Integer autoappid;

                                if(activity.getApplicationId().isPresent()){
                                    //Variables for Application
                                    String applicationname = activity.getName();
                                    long application = activity.getApplicationId().get().asLong();
                                    //Look up, if application exists in DB
                                    autoappid = readFromSQL.searchApplicationById(application);
                                    //Insert application, if application does not exist.
                                    if (autoappid == null) {
                                        try {
                                            autoappid = writeToSQL.insertApplication(applicationname, application);
                                        } catch (SQLException ex) {
                                            return Mono.error(new RuntimeException(ex));
                                        }
                                    }
                                } else {
                                    //Variables for Application
                                    String applicationname = activity.getName();
                                    //Look up, if application exists in DB
                                    autoappid = readFromSQL.searchApplicationByName(applicationname);
                                    //Insert application, if application does not exist.
                                    if (autoappid == null) {
                                        try {
                                            autoappid = writeToSQL.insertApplicationNameOnly(applicationname);
                                        } catch (SQLException ex) {
                                            return Mono.error(new RuntimeException(ex));
                                        }
                                    }
                                }


                                //Variables for AppState
                                String app_state = null;;
                                if (activity.getState().isPresent()) {
                                     app_state = activity.getState().get();
                                }

                                String app_details = null;
                                if (activity.getDetails().isPresent()) {
                                    app_details = activity.getDetails().get();
                                }

                                //Look up, if App State exists in DB
                                Integer autoappstateid = readFromSQL.searchAppState(app_state, app_details);
                                //Insert details and state, if App State does not exist.
                                if (autoappstateid == null) {
                                    try {
                                        autoappstateid = writeToSQL.insertAppState(app_details, app_state);
                                    } catch (SQLException ex) {
                                        return Mono.error(new RuntimeException(ex));
                                    }
                                }


                                //Variables for Type
                                String type = activity.getType().name();
                                //Look up, if Type exists in DB
                                Integer autotypeid = readFromSQL.searchType(type);
                                //Insert details and state, if App State does not exist.
                                if (autotypeid == null) {
                                    try {
                                        autotypeid = writeToSQL.insertType(type);
                                    } catch (SQLException ex) {
                                        return Mono.error(new RuntimeException(ex));
                                    }
                                }


                                //write everything to SQL
                                try {
                                    activityIds.add(writeToSQL.insertActivity(autoappid, autouserid, autostatusid, autoappstateid, autotypeid, currenttime));
                                } catch (SQLException ex) {
                                    return Mono.error(new RuntimeException(ex));
                                }

                                content.append("\n- Name: ").append(activity.getName())
                                       .append("\n  Type: ").append(activity.getType().name());

                                appendIfPresent(content, "Details", activity.getDetails());
                                appendIfPresent(content, "State", activity.getState());
                                appendIfPresent(content, "Start", activity.getStart());
                                appendIfPresent(content, "Application ID", activity.getApplicationId().map(Snowflake::asString));
                            }
                        } else {
                            /*
                            try {
                                updatePrevActivity(readFromSQL, writeToSQL, autouserid, currenttime, activityIds);
                            } catch (SQLException ex) {
                                return Mono.error(new RuntimeException(ex));
                            }*/
                            try {
                                activityIds.add(writeToSQL.insertActivitySlim(autouserid, autostatusid, currenttime));
                            } catch (SQLException ex) {
                                return Mono.error(new RuntimeException(ex));
                            }
                        }
                                        try {
                                            updatePrevActivity(readFromSQL, writeToSQL, autouserid, currenttime, activityIds);
                                        } catch (SQLException ex) {
                                            return Mono.error(new RuntimeException(ex));
                                        }
                                        try {
                                            assert conn != null;
                                            conn.close();
                                        } catch (SQLException ex) {
                                            return Mono.error(new RuntimeException(ex));
                                        }


                                        return ((MessageChannel) Objects.requireNonNull(client.getChannelById(Snowflake.of("1008364168753193030")).block())).createMessage(content.toString());
                    }).then();

                });
    }

    private void updatePrevActivity(ReadFromSQL readFromSQL, WriteToSQL writeToSQL, int autouserid, Timestamp currenttime, ArrayList<Integer> activityIds) throws SQLException{
        // if new activity of user XY, then insert current time to endtime
        ArrayList<Integer> prev_activity = readFromSQL.searchNewestActivityByUser(autouserid, activityIds);
        //Timestamp currenttime = Timestamp.from(activity.getStart().get());
        if (!(prev_activity == null)){
            writeToSQL.updateEndActivity(prev_activity, currenttime);
        }
    }

    private <T> void appendIfPresent(StringBuilder sb, String label, Optional<T> value) {
        value.ifPresent(v -> sb.append("\n  ").append(label).append(": ").append(v));
    }
}
