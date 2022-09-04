package ch.wema.reactions;

import ch.wema.core.reaction.Reaction;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class AtEveryOneReaction implements Reaction<MessageCreateEvent> {
    @Override
    public Mono<Void> handle(MessageCreateEvent event) {
        Mono.just(event)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().toLowerCase().contains("everyone"))
                .subscribe(message -> message.getAuthor().ifPresent(user -> {
                    var member = user.asMember(message.getGuildId().get()).block();
                    member.addRole(Snowflake.of("1008358209679007825")).block();

                    var content = message.getContent();
                    var userData = message.getUserData();
                    var timestamp = message.getTimestamp();

                    var msg = "Timestamp: '" + timestamp + "'\n"
                            + "User: '" + userData.username() + "#" + userData.discriminator() + "' UserId: '" + userData.id() + "'\n"
                            + "Message: '" + content + "'";

                    /*
                    var botlogs = (MessageChannel) client.getChannelById(Snowflake.of("1008364168753193030")).block();
                    botlogs.createMessage(msg).block();
                    */

                    message.delete().block();
                }));

        return Mono.empty();
    }
}
