package ch.wema.reactions;

import ch.wema.core.reaction.Reaction;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.Activity;
import reactor.core.publisher.Mono;

public class StalkReaction implements Reaction<PresenceUpdateEvent> {
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
                        StringBuilder content = new StringBuilder("The Status of the user " + u.getUsername() + " (" + u.getId().asString() + ") changed to " + status + ".");

                        // If the user has any activities, append them to the log message
                        if (!event.getCurrent().getActivities().isEmpty()) {
                            content.append("\nActivities:");
                            for (Activity activity : event.getCurrent().getActivities()) {
                                /* 
                                If 
                                Name: Custom Status or
                                Type: CUSTOM
                                We can ignore the activity only user status information
                                */
                                content.append("\n- Name: ").append(activity.getName())
                                        .append("\n- Type: ").append(activity.getType().name());

                                if (activity.getDetails().isPresent()) {
                                    content.append("\n- Details: ").append(activity.getDetails().get());
                                }

                                if (activity.getState().isPresent()) {
                                    content.append("\n- State: ").append(activity.getState().get());
                                }

                                if (activity.getStart().isPresent()) {
                                    content.append("\n- Start: ").append(activity.getStart().get());
                                }

                                if (activity.getEnd().isPresent()) {
                                    content.append("\n- End: ").append(activity.getEnd().get());
                                }

                                if (activity.getApplicationId().isPresent()) {
                                    content.append("\n- Application ID: ").append(activity.getApplicationId().get().asString());
                                }
                            }
                        }

                        return ((MessageChannel) client.getChannelById(Snowflake.of("1008364168753193030")).block()).createMessage(content.toString());
                    }).then();

                });
    }
}
