package ch.wema.reactions;

import ch.wema.core.reaction.Reaction;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.PresenceUpdateEvent;
import reactor.core.publisher.Mono;

public class StalkReaction implements Reaction<PresenceUpdateEvent> {
    @Override
    public Mono<Void> handle(PresenceUpdateEvent presenceUpdateEvent) {
        Mono
                .just(presenceUpdateEvent)
                .subscribe(e -> {
                    String userId = e.getUserId().asString();
                })
                ;
        return Mono.empty();
    }
}
