package ch.wema;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

public class Main {


    public static void main(final String[] args) {
        final var token = System.getenv("DISCORD_CLIENT_TOKEN");
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            if ("!ping".equals(message.getContent())) {
                final MessageChannel channel = message.getChannel().block();
                channel.createMessage("Pong!").block();
            }
            if ("!version".equals(message.getContent())){
                final MessageChannel channel = message.getChannel().block();
                channel.createMessage("New version").block();
            }
        });

        gateway.onDisconnect().block();
    }
}