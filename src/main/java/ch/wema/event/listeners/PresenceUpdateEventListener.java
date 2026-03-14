package ch.wema.event.listeners;

import ch.wema.core.reaction.Reaction;
import ch.wema.reactions.ActivityReaction;
import discord4j.core.event.domain.PresenceUpdateEvent;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
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
 * Solution: Global in-memory cache mapping (userId + status + activitiesFingerprint) to timestamp.
 * Events with matching key within DEDUP_WINDOW_MS are skipped.
 *
 * Cache behavior:
 * - Key format: "userId:status:activityFingerprint" (see buildDedupKey)
 * - Entries persist until overwritten by same key or cleanup runs
 * - Cleanup only triggers when cache exceeds 1000 entries, removes entries older than 2x window
 * - The window only affects dedup check, not cache lifetime (old entries still exist but don't block)
 *
 * Key composition ensures different activities create different keys,
 * so genuine activity changes are never skipped.
 */
public class PresenceUpdateEventListener {

    //private static final Logger LOGGER = LoggerFactory.getLogger(PresenceUpdateEventListener.class);
    private final static List<Reaction<PresenceUpdateEvent>> reactions = new ArrayList<>();

    /**
     * Global cache: dedupKey -> timestamp of last processed event.
     * Shared across all users, but keys include userId so users don't interfere.
     */
    private static final ConcurrentHashMap<String, Long> recentPresenceUpdates = new ConcurrentHashMap<>();

    /**
     * Window in which duplicate events are ignored.
     * Must be longer than event processing time (~1-2s with DB + Discord API calls).
     * Events with identical keys arriving within this window are skipped.
     */
    private static final long DEDUP_WINDOW_MS = 3000;

    static {
        reactions.add(new ActivityReaction());
    }

    public static Mono<Void> handle(PresenceUpdateEvent event) {
        String dedupKey = buildDedupKey(event);
        long now = System.currentTimeMillis();

        //LOGGER.debug("Dedup: Received event for user {} | key: {}",
        //        event.getUserId().asString(), dedupKey);

        // Check if we recently processed an identical event
        Long lastSeen = recentPresenceUpdates.get(dedupKey);
        if (lastSeen != null && (now - lastSeen) < DEDUP_WINDOW_MS) {
            //LOGGER.debug("Dedup: SKIPPING duplicate for user {} ({}ms since last)",
            //        event.getUserId().asString(), now - lastSeen);
            return Mono.empty();
        }

        //LOGGER.debug("Dedup: PROCESSING event for user {}", event.getUserId().asString());
        // Record this event as processed
        recentPresenceUpdates.put(dedupKey, now);

        // Prevent unbounded memory growth - cleanup when map gets large
        cleanupOldEntries(now);

        return Flux.fromIterable(reactions)
                .flatMap(reaction -> reaction.handle(event))
                .then();
    }

    /**
     * Key = userId:status:activitiesFingerprint
     * Different activities produce different fingerprint → different key → processed normally
     */
    private static String buildDedupKey(PresenceUpdateEvent event) {
        var presence = event.getCurrent();
        String activitiesFingerprint = buildActivitiesFingerprint(presence.getActivities());
        return event.getUserId().asString() + ":" + presence.getStatus() + ":" + activitiesFingerprint;
    }

    /**
     * Builds a stable fingerprint from activity contents.
     * Discord4J Activity objects don't have stable hashCode(), so we build our own.
     */
    private static String buildActivitiesFingerprint(java.util.List<discord4j.core.object.presence.Activity> activities) {
        if (activities.isEmpty()) {
            return "none";
        }
        StringBuilder sb = new StringBuilder();
        for (var activity : activities) {
            sb.append(activity.getName()).append("|");
            sb.append(activity.getType().name()).append("|");
            activity.getApplicationId().ifPresent(id -> sb.append(id.asString()));
            sb.append("|");
            activity.getDetails().ifPresent(sb::append);
            sb.append("|");
            activity.getState().ifPresent(sb::append);
            sb.append(";");
        }
        return sb.toString();
    }

    /**
     * Lazy cleanup to prevent unbounded memory growth.
     * Only runs when cache exceeds 1000 entries (not on every event).
     * Removes entries older than 2x the dedup window (6 seconds at current config).
     * Old entries don't affect dedup logic - they just occupy memory until cleaned.
     */
    private static void cleanupOldEntries(long now) {
        if (recentPresenceUpdates.size() > 1000) {
            recentPresenceUpdates.entrySet().removeIf(e -> (now - e.getValue()) > DEDUP_WINDOW_MS * 2);
        }
    }
}
