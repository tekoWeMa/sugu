package ch.wema;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

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

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().toLowerCase().contains("@everyone"))
                .subscribe(message -> message.getAuthor().ifPresent(user -> {
                    var member = user.asMember(message.getGuildId().get()).block();
                    member.addRole(Snowflake.of("1008358209679007825")).block();

                    var content = message.getContent();
                    var userData = message.getUserData();
                    var timestamp = message.getTimestamp();

                    var msg = "Timestamp: '" + timestamp + "'\n"
                            + "User: '" + userData.username() + "#" + userData.discriminator() + "' UserId: '" + userData.id() + "'\n"
                            + "Message: '" + content + "'";

                    var botlogs = (MessageChannel) client.getChannelById(Snowflake.of("1008364168753193030")).block();
                    botlogs.createMessage(msg).block();

                    message.delete().block();
                })); //Store message

        /*
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().equalsIgnoreCase("!ids"))
                .flatMap(state -> {
                    var guild = state.getGuild().block();

                    System.out.println("Roles:");
                    guild.getRoles().map(role -> {
                        var id = role.getId();
                        var name = role.getName();

                        var message = "ID: '" + id + "' Name:'" + name + "'";
                        System.out.println(message);

                        return message;
                    }).subscribe();

                    System.out.println("Channels:");
                    guild.getChannels().map(channel -> {
                        var id = channel.getId();
                        var name = channel.getName();

                        var message = "ID: '" + id + "' Name:'" + name + "'";
                        System.out.println(message);

                        return message;
                    }).subscribe();

                    var channel = state.getChannel().block();
                    return channel.createMessage("Dont!");
                })
                .subscribe();
        */

        client.getEventDispatcher().on(VoiceStateUpdateEvent.class)
                .map(VoiceStateUpdateEvent::getCurrent)
                .filter(state -> state.getChannelId().map(id -> id.equals(Snowflake.of("619989000693874699"))).orElse(false))
                .subscribe(state -> {
                    var member = state.getMember().block();
                    var message = member.toString();

                    member.addRole(Snowflake.of("774385363112296488")).block();

                    System.out.println(message);
                });

        client.onDisconnect().block();
    }
}