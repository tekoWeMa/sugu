package ch.wema.reactions;

import ch.wema.core.reaction.Reaction;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public class YepReaction implements Reaction<MessageCreateEvent> {

    @Override
    public Mono<Void> handle(MessageCreateEvent messageCreateEvent) {
        Mono.just(messageCreateEvent)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().toLowerCase().contains("yep"))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("YEP"))
                .subscribe();

        return Mono.empty();
    }
}
