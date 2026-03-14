package ch.wema.reactions;

import ch.wema.core.reaction.Reaction;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class AtEveryOneReaction implements Reaction<MessageCreateEvent> {
    @Override
    public Mono<Void> handle(MessageCreateEvent event) {
        return Mono.just(event)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().toLowerCase().contains("everyone"))
                .filter(message -> message.getGuildId().isPresent())
                .flatMap(message -> Mono.justOrEmpty(message.getAuthor())
                        .flatMap(user -> user.asMember(message.getGuildId().get()))
                        .flatMap(member -> member.addRole(Snowflake.of("1008358209679007825")))
                        .then(message.delete()))
                .then();
    }
}
