package ch.wema.loggers;

import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.User;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.MessageChannel;
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
                    String content = "The Status of the user " + user.getUsername() + " (" + user.getId().asString() + ") changed to " + status + ".";
                    return logChannel.createMessage(content);
                })
                .then();
    }
}


