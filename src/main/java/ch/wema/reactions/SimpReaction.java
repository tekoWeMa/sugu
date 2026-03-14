package ch.wema.reactions;

import ch.wema.core.reaction.Reaction;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import reactor.core.publisher.Mono;

public class SimpReaction implements Reaction<VoiceStateUpdateEvent> {

    @Override
    public Mono<Void> handle(VoiceStateUpdateEvent event) {
        return Mono.just(event)
                .map(VoiceStateUpdateEvent::getCurrent)
                .filter(state -> state.getChannelId().map(id -> id.equals(Snowflake.of("619989000693874699"))).orElse(false))
                .flatMap(state -> state.getMember())
                .flatMap(member -> member.addRole(Snowflake.of("774385363112296488")))
                .then();
    }
}
