package ch.wema.event.listeners;

import ch.wema.reactions.GuildJoinReaction;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

public class GuildCreateEventListener {

    private static final GuildJoinReaction guildJoinReaction = new GuildJoinReaction();
    private static final Set<String> knownGuilds = new HashSet<>();
    private static boolean initialized = false;

    public static Mono<Void> handle(GuildCreateEvent event) {
        String guildId = event.getGuild().getId().asString();

        // GuildCreateEvent fires for ALL guilds on startup
        // Only notify for truly NEW guilds (after initial load)
        if (!initialized) {
            knownGuilds.add(guildId);
            return Mono.empty();
        }

        if (knownGuilds.contains(guildId)) {
            return Mono.empty();
        }

        knownGuilds.add(guildId);
        return guildJoinReaction.handle(event);
    }

    public static void markInitialized() {
        initialized = true;
    }
}
