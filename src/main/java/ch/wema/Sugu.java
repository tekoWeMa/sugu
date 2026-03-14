package ch.wema;

import ch.wema.SQL.DatabaseService;
import ch.wema.SQL.WriteToSQL;
import ch.wema.event.listeners.*;
import ch.wema.presence.BotPresenceManager;
import ch.wema.reactions.ActivityReaction;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.gateway.intent.IntentSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
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

        BotPresenceManager presenceManager = new BotPresenceManager(client);
        presenceManager.start();
        ChatInputInteractionEventListener.initialize(presenceManager);

        List<String> commands = List.of("ping.json", "status.json", "servers.json", "invite.json");
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
        try (Connection conn = DatabaseService.getConnection()) {
            WriteToSQL writeToSQL = new WriteToSQL(conn);
            writeToSQL.deleteEmptyEndDates();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseService::shutdown));

        client.on(ChatInputInteractionEvent.class, ChatInputInteractionEventListener::handle).subscribe();
        client.on(MessageCreateEvent.class, MessageCreateEventListener::handle).subscribe();
        client.on(PresenceUpdateEvent.class, PresenceUpdateEventListener::handle).subscribe();
        client.on(VoiceStateUpdateEvent.class, VoiceStateUpdateEventListener::handle).subscribe();
        client.on(GuildCreateEvent.class, GuildCreateEventListener::handle).subscribe();

        // Mark guild listener as initialized after a delay to allow initial guild loading
        client.getGuilds().count()
                .doOnSuccess(count -> {
                    LOGGER.info("Bot connected to {} servers", count);
                    GuildCreateEventListener.markInitialized();
                })
                .subscribe();

        client.onDisconnect().block();
    }
}