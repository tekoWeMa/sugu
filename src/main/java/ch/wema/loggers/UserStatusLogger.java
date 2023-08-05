package ch.wema.loggers;

import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.User;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.Activity;
import reactor.core.publisher.Mono;


public class UserStatusLogger {

    private final MessageChannel logChannel;
    private final GatewayDiscordClient client;

    public UserStatusLogger(GatewayDiscordClient client, Snowflake logChannelId) {
        // Store the client for later use
        this.client = client;


        // Get the log channel when the instance is created
        this.logChannel = (MessageChannel) client.getChannelById(logChannelId).block();
    }

    public Mono<Void> handle(PresenceUpdateEvent event) {
        return Mono.just(event)
                .flatMap(e -> {
                    String userId = e.getUserId().asString();
                    return client.getUserById(Snowflake.of(userId));
                })
                .flatMap(user -> {
                    // Extract the user's status from their status
                    String status = event.getCurrent().getStatus().toString();

                    // Build the base log message
                    StringBuilder content = new StringBuilder("The Status of the user " + user.getUsername() + " (" + user.getId().asString() + ") changed to " + status + ".");

                    // If the user has any activities, append them to the log message
                    if (!event.getCurrent().getActivities().isEmpty()) {
                        content.append("\nActivities:");
                        for (Activity activity : event.getCurrent().getActivities()) {
                            content.append("\n- Name: ").append(activity.getName())
                                    .append("\n- Type: ").append(activity.getType().name());

                            if (activity.getDetails().isPresent()) {
                                content.append("\n- Details: ").append(activity.getDetails().get());
                            }

                            if (activity.getState().isPresent()) {
                                content.append("\n- State: ").append(activity.getState().get());
                            }

                            if (activity.getStart().isPresent()) {
                                content.append("\n- Start: ").append(activity.getStart().get().toString());
                            }

                            if (activity.getEnd().isPresent()) {
                                content.append("\n- End: ").append(activity.getEnd().get().toString());
                            }

                            if (activity.getApplicationId().isPresent()) {
                                content.append("\n- Application ID: ").append(activity.getApplicationId().get().asString());
                            }
                        }
                    }

                    return logChannel.createMessage(content.toString());
                })
                .then();
    }
}


