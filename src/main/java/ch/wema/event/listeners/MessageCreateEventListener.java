package ch.wema.event.listeners;

import ch.wema.core.reaction.Reaction;
import ch.wema.reactions.AtEveryOneReaction;
import ch.wema.reactions.YepReaction;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class MessageCreateEventListener {
    private final static List<Reaction<MessageCreateEvent>> reactions = new ArrayList<>();

    static {
        reactions.add(new AtEveryOneReaction());
        reactions.add(new YepReaction());
    }

    public static Mono<Void> handle(MessageCreateEvent event) {
        return Flux.fromIterable(reactions)
                .flatMap(reaction -> reaction.handle(event))
                .next();
    }
}
