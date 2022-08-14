package ch.wema;

import ch.wema.listeners.CommandListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {
        final var token = System.getenv("DISCORD_CLIENT_TOKEN");
        final GatewayDiscordClient client = DiscordClientBuilder.create(token)
                .build()
                .login()
                .block();

        List<String> commands = List.of("ping.json");
        try {
            new GlobalCommandRegistrar(client.getRestClient()).registerCommands(commands);
        } catch (Exception e) {
            LOGGER.error("Error trying to register global slash commands", e);
        }

        client.on(ChatInputInteractionEvent.class, CommandListener::handle)
                .then(client.onDisconnect())
                .block();
    }
}