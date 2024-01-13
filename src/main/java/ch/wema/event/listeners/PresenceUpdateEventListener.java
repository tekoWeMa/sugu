package ch.wema.event.listeners;

import ch.wema.core.reaction.Reaction;
import ch.wema.reactions.ActivityReaction;
import discord4j.core.event.domain.PresenceUpdateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class PresenceUpdateEventListener {

    private final static List<Reaction<PresenceUpdateEvent>> reactions = new ArrayList<>();

    static {
        reactions.add(new ActivityReaction());
    }

    public static Mono<Void> handle(PresenceUpdateEvent event) {
        return Flux.fromIterable(reactions)
                .flatMap(reaction -> reaction.handle(event))
                .next();
    }
}
