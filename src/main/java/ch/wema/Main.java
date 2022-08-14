package ch.wema;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;

public class Main {


    public static void main(final String[] args) {
        final var token = System.getenv("DISCORD_CLIENT_TOKEN");
        final var client = DiscordClientBuilder.create(token).build().login().block();

        //PING command
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().equalsIgnoreCase("!ping"))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Pong!"))
                .subscribe(); //ToDo latency

        //YEP reaction
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().toLowerCase().contains("yep"))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("YEP"))
                .subscribe();

        client.onDisconnect().block();
    }
}