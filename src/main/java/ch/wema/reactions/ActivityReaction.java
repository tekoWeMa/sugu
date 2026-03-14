package ch.wema.reactions;

import ch.wema.SQL.DatabaseService;
import ch.wema.SQL.ReadFromSQL;
import ch.wema.SQL.WriteToSQL;
import ch.wema.core.reaction.Reaction;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActivityReaction implements Reaction<PresenceUpdateEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityReaction.class);

    @Override
    public Mono<Void> handle(PresenceUpdateEvent event) {
        //check if this is even getting used
        return Mono.just(event)
                .flatMap(e -> {
                    String userId = e.getUserId().asString();
                    var client = e.getClient();
                    return client.getUserById(Snowflake.of(userId)).flatMap(u -> {
                        // Extract the user's status from their status
                        String status = event.getCurrent().getStatus().toString();
                        List<Activity> activities = event.getCurrent().getActivities();

                        return Mono.fromCallable(() -> {
                            // Build the base log message
                            StringBuilder content = new StringBuilder();
                            content.append("The Status of the user ")
                                   .append(u.getUsername())
                                   .append(" (").append(u.getId().asString()).append(") ")
                                   .append("changed to ").append(status).append(".");

                            //Make Connection to DB
                            try (Connection conn = DatabaseService.getConnection()) {
                                ReadFromSQL readFromSQL = new ReadFromSQL(conn);
                                WriteToSQL writeToSQL = new WriteToSQL(conn);
                                //Get Current Time
                                Timestamp currenttime = new Timestamp(new java.util.Date().getTime());
                                //Variables for User
                                long userID = u.getId().asLong();
                                String username = u.getUsername();
                                //Look up, if User exists in DB
                                Integer autouserid = readFromSQL.searchUser(userID);
                                //Insert User, if User does not exist.
                                if (autouserid == null) {
                                    autouserid = writeToSQL.insertUser(username, userID);
                                }

                                //Variables for Statustype
                                //Look up, if Status exists in DB
                                Integer autostatusid = readFromSQL.searchStatus(status);
                                //Insert Status, if Status does not exist.
                                if (autostatusid == null) {
                                    autostatusid = writeToSQL.insertStatus(status);
                                }

                                ArrayList<Integer> activityIds = new ArrayList<>();
                                // If the user has any activities, append them to the log message
                                if (!activities.isEmpty()) {
                                    content.append("\nActivities:");
                                    for (Activity activity : activities) {
                                        /*
                                        If
                                        Name: Custom Status or
                                        Type: CUSTOM
                                        We can ignore the activity only user status information
                                        */
                                        if (activity.getName().equals("Custom Status") || activity.getType().name().equals("CUSTOM")) {
                                            if (activities.size() == 1) {
                                                updatePrevActivity(readFromSQL, writeToSQL, autouserid, currenttime, activityIds);
                                                writeToSQL.insertActivitySlim(autouserid, autostatusid, currenttime);
                                            }
                                            continue;
                                        }

                                        Integer autoappid;
                                        if (activity.getApplicationId().isPresent()) {
                                            //Variables for Application
                                            String applicationname = activity.getName();
                                            long application = activity.getApplicationId().get().asLong();
                                            //Look up, if application exists in DB
                                            autoappid = readFromSQL.searchApplicationById(application);
                                            //Insert application, if application does not exist.
                                            if (autoappid == null) {
                                                autoappid = writeToSQL.insertApplication(applicationname, application);
                                            }
                                        } else {
                                            //Variables for Application
                                            String applicationname = activity.getName();
                                            //Look up, if application exists in DB
                                            autoappid = readFromSQL.searchApplicationByName(applicationname);
                                            //Insert application, if application does not exist.
                                            if (autoappid == null) {
                                                autoappid = writeToSQL.insertApplicationNameOnly(applicationname);
                                            }
                                        }

                                        //Variables for AppState
                                        String app_state = activity.getState().orElse(null);
                                        String app_details = activity.getDetails().orElse(null);

                                        //Look up, if App State exists in DB
                                        Integer autoappstateid = readFromSQL.searchAppState(app_state, app_details);
                                        //Insert details and state, if App State does not exist.
                                        if (autoappstateid == null) {
                                            autoappstateid = writeToSQL.insertAppState(app_details, app_state);
                                        }

                                        //Variables for Type
                                        String type = activity.getType().name();
                                        //Look up, if Type exists in DB
                                        Integer autotypeid = readFromSQL.searchType(type);
                                        //Insert Type, if Type does not exist.
                                        if (autotypeid == null) {
                                            autotypeid = writeToSQL.insertType(type);
                                        }

                                        //Variables for Party
                                        String partyId = activity.getPartyId().orElse(null);
                                        Integer partySize = activity.getCurrentPartySize().isPresent() ? (int) activity.getCurrentPartySize().getAsLong() : null;
                                        Integer partyMax = activity.getMaxPartySize().isPresent() ? (int) activity.getMaxPartySize().getAsLong() : null;

                                        //write everything to SQL
                                        activityIds.add(writeToSQL.insertActivity(autoappid, autouserid, autostatusid, autoappstateid, autotypeid, currenttime, partyId, partySize, partyMax));

                                        content.append("\n- Name: ").append(activity.getName())
                                               .append("\n  Type: ").append(activity.getType().name());
                                        appendIfPresent(content, "Details", activity.getDetails());
                                        appendIfPresent(content, "State", activity.getState());
                                        appendIfPresent(content, "Start", activity.getStart());
                                        appendIfPresent(content, "End", activity.getEnd());
                                        appendIfPresent(content, "Application ID", activity.getApplicationId().map(Snowflake::asString));
                                        appendIfPresent(content, "Party ID", activity.getPartyId());
                                        appendIfPresent(content, "Party Size", activity.getCurrentPartySize().isPresent() ? Optional.of(activity.getCurrentPartySize().getAsLong() + "/" + activity.getMaxPartySize().orElse(0)) : Optional.empty());
                                        appendIfPresent(content, "Streaming URL", activity.getStreamingUrl());
                                        appendIfPresent(content, "Instance", activity.isInstance() ? Optional.of(true) : Optional.empty());
                                        appendIfPresent(content, "Join Secret", activity.getJoinSecret());
                                        appendIfPresent(content, "Spectate Secret", activity.getSpectateSecret());
                                        appendIfPresent(content, "Match Secret", activity.getMatchSecret());
                                    }
                                } else {
                                    activityIds.add(writeToSQL.insertActivitySlim(autouserid, autostatusid, currenttime));
                                }
                                updatePrevActivity(readFromSQL, writeToSQL, autouserid, currenttime, activityIds);
                            }
                            return content;
                        })
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(content -> client.getChannelById(Snowflake.of("1008364168753193030"))
                                .flatMap(channel -> ((MessageChannel) channel).createMessage(content.toString())));
                    });
                })
                .then();
    }

    private void updatePrevActivity(ReadFromSQL readFromSQL, WriteToSQL writeToSQL, int autouserid, Timestamp currenttime, ArrayList<Integer> activityIds) throws SQLException {
        // if new activity of user XY, then insert current time to endtime
        ArrayList<Integer> prev_activity = readFromSQL.searchNewestActivityByUser(autouserid, activityIds);
        if (prev_activity != null) {
            writeToSQL.updateEndActivity(prev_activity, currenttime);
        }
    }

    private <T> void appendIfPresent(StringBuilder sb, String label, Optional<T> value) {
        value.ifPresent(v -> sb.append("\n  ").append(label).append(": ").append(v));
    }
}
