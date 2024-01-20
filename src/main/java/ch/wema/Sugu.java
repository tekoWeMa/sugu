package ch.wema;

import ch.wema.SQL.DBConnection;
import ch.wema.SQL.WriteToSQL;
import ch.wema.event.listeners.*;
import ch.wema.reactions.ActivityReaction;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.gateway.intent.IntentSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Sugu {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sugu.class);

    public static void main(final String[] args) {
        final var token = System.getenv("DISCORD_CLIENT_TOKEN"); // Here comes the Token, remember to not commit this to the Repo.
        final GatewayDiscordClient client = DiscordClientBuilder
                .create(token)
                .build()
                .gateway()
                .setEnabledIntents(IntentSet.all())
                .login()
                .block();

        List<String> commands = List.of("ping.json");
        try {
            new GlobalCommandRegistrar(client.getRestClient()).registerCommands(commands);
        } catch (Exception e) {
            LOGGER.error("Error trying to register global slash commands", e);
        }
        // TODO: debug/troubleshoot this; maybe do ashe did and check again; see UserStatusLoggerEventListener
        //
        // Create EventListener instance and start listening
        //ORIG
        //UserStatusLoggerEventListener eventListener = new UserStatusLoggerEventListener(client);
        //eventListener.startListening();


        client.on(ChatInputInteractionEvent.class, ChatInputInteractionEventListener::handle)
                .then(client.onDisconnect())
                .subscribe();
        client.on(MessageCreateEvent.class, MessageCreateEventListener::handle)
                .then(client.onDisconnect())
                .subscribe();
        client.on(PresenceUpdateEvent.class, PresenceUpdateEventListener::handle)
                .then(client.onDisconnect())
                .subscribe();
        client.on(VoiceStateUpdateEvent.class, VoiceStateUpdateEventListener::handle)
                .then(client.onDisconnect())
                .subscribe();

        client.onDisconnect().block();
    }
}