package ch.wema.event.listeners;

import ch.wema.loggers.UserStatusLogger;
import discord4j.core.GatewayDiscordClient;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.PresenceUpdateEvent;

public class UserStatusLoggerEventListener {

    private final UserStatusLogger userStatusLogger;
    private final GatewayDiscordClient client;

    public UserStatusLoggerEventListener(GatewayDiscordClient client) {
        // Create UserStatusLogger instance
        this.client = client;
        userStatusLogger = new UserStatusLogger(client, Snowflake.of("1008364168753193030"));
    }

    public void startListening() {
        // Start listening for PresenceUpdateEvent
        client.on(PresenceUpdateEvent.class, userStatusLogger::handle)
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }
}
