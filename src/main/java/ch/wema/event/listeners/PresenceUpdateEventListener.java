package ch.wema.event.listeners;

import ch.wema.core.reaction.Reaction;
import ch.wema.reactions.ActivityReaction;
import discord4j.core.event.domain.PresenceUpdateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Deduplication Strategy:
 * Discord fires PresenceUpdateEvent once per guild the user shares with the bot.
 * A user in 5 guilds triggers 5 identical events for the same presence change.
 *
 * Solution: Cache (userId + status + activitiesHash) with timestamp.
 * Events with matching key within DEDUP_WINDOW_MS are skipped.
 *
 * Key composition ensures different activities create different keys,
 * so genuine activity changes are never skipped.
 */
public class PresenceUpdateEventListener {

    private final static List<Reaction<PresenceUpdateEvent>> reactions = new ArrayList<>();

    // Maps dedupKey -> timestamp of last processed event
    private static final ConcurrentHashMap<String, Long> recentPresenceUpdates = new ConcurrentHashMap<>();

    // Window in which duplicate events are ignored (500ms covers multi-guild event burst)
    private static final long DEDUP_WINDOW_MS = 500;

    static {
        reactions.add(new ActivityReaction());
    }

    public static Mono<Void> handle(PresenceUpdateEvent event) {
        String dedupKey = buildDedupKey(event);
        long now = System.currentTimeMillis();

        // Check if we recently processed an identical event
        Long lastSeen = recentPresenceUpdates.get(dedupKey);
        if (lastSeen != null && (now - lastSeen) < DEDUP_WINDOW_MS) {
            // Duplicate event from another guild - skip
            return Mono.empty();
        }

        // Record this event as processed
        recentPresenceUpdates.put(dedupKey, now);

        // Prevent unbounded memory growth - cleanup when map gets large
        cleanupOldEntries(now);

        return Flux.fromIterable(reactions)
                .flatMap(reaction -> reaction.handle(event))
                .next();
    }

    /**
     * Key = userId:status:activitiesHashCode
     * Different activities produce different hash → different key → processed normally
     */
    private static String buildDedupKey(PresenceUpdateEvent event) {
        var presence = event.getCurrent();
        int activitiesHash = presence.getActivities().hashCode();
        return event.getUserId().asString() + ":" + presence.getStatus() + ":" + activitiesHash;
    }

    /**
     * Removes expired entries when map exceeds threshold.
     * Runs lazily to avoid overhead on every event.
     */
    private static void cleanupOldEntries(long now) {
        if (recentPresenceUpdates.size() > 1000) {
            recentPresenceUpdates.entrySet().removeIf(e -> (now - e.getValue()) > DEDUP_WINDOW_MS * 2);
        }
    }
}
