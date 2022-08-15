package ch.wema.event.listeners;

import ch.wema.core.reaction.Reaction;
import ch.wema.reactions.SimpReaction;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class VoiceStateUpdateEventListener {
    private final static List<Reaction<VoiceStateUpdateEvent>> reactions = new ArrayList<>();

    static {
        reactions.add(new SimpReaction());
    }

    public static Mono<Void> handle(VoiceStateUpdateEvent event) {
        return Flux.fromIterable(reactions)
                .flatMap(reaction -> reaction.handle(event))
                .next();
    }
}
