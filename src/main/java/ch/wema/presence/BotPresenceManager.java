package ch.wema.presence;

import ch.wema.SQL.DatabaseService;
import ch.wema.SQL.ReadFromSQL;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class BotPresenceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotPresenceManager.class);
    private static final Duration ROTATION_INTERVAL = Duration.ofMinutes(5);
    private static final List<StatusPreset> ROTATION_PRESETS = List.of(
            StatusPreset.STATS,
            StatusPreset.ACTIVITIES_TODAY,
            StatusPreset.USERS,
            StatusPreset.SERVERS,
            StatusPreset.HELP,
            StatusPreset.LISTENING,
            StatusPreset.INVITE
    );

    private final GatewayDiscordClient client;
    private final AtomicReference<Mode> currentMode = new AtomicReference<>(Mode.ROTATING);
    private final AtomicReference<StatusPreset> currentPreset = new AtomicReference<>(null);
    private final AtomicReference<ClientActivity> customActivity = new AtomicReference<>(null);
    private final AtomicInteger rotationIndex = new AtomicInteger(0);
    private Disposable rotationDisposable;

    public enum Mode {
        ROTATING,
        PRESET,
        CUSTOM
    }

    public BotPresenceManager(GatewayDiscordClient client) {
        this.client = client;
    }

    public void start() {
        startRotation();
    }

    public void stop() {
        stopRotation();
    }

    public Mono<Void> setRotating() {
        currentMode.set(Mode.ROTATING);
        currentPreset.set(null);
        customActivity.set(null);
        startRotation();
        return updatePresenceNow();
    }

    public Mono<Void> setPreset(StatusPreset preset) {
        stopRotation();
        currentMode.set(Mode.PRESET);
        currentPreset.set(preset);
        customActivity.set(null);
        return fetchStats()
                .map(stats -> preset.createActivity(stats))
                .flatMap(this::updatePresenceWithActivity);
    }

    public Mono<Void> setCustom(ClientActivity activity) {
        stopRotation();
        currentMode.set(Mode.CUSTOM);
        currentPreset.set(null);
        customActivity.set(activity);
        return updatePresenceWithActivity(activity);
    }

    public Mode getCurrentMode() {
        return currentMode.get();
    }

    public StatusPreset getCurrentPreset() {
        return currentPreset.get();
    }

    public ClientActivity getCustomActivity() {
        return customActivity.get();
    }

    public String getCurrentStatusDescription() {
        return switch (currentMode.get()) {
            case ROTATING -> "Rotating (every 5 min)";
            case PRESET -> "Preset: " + currentPreset.get().getName();
            case CUSTOM -> "Custom status";
        };
    }

    private void startRotation() {
        stopRotation();
        LOGGER.info("Starting presence rotation");
        rotationDisposable = Flux.interval(Duration.ZERO, ROTATION_INTERVAL)
                .doOnNext(tick -> LOGGER.debug("Presence rotation tick: {}", tick))
                .flatMap(tick -> updatePresenceNow()
                        .doOnError(e -> LOGGER.error("Failed to update presence", e))
                        .onErrorResume(e -> Mono.empty()))
                .subscribe(
                        unused -> {},
                        error -> LOGGER.error("Error in presence rotation", error)
                );
    }

    private void stopRotation() {
        if (rotationDisposable != null && !rotationDisposable.isDisposed()) {
            rotationDisposable.dispose();
        }
    }

    private Mono<Void> updatePresenceNow() {
        if (currentMode.get() != Mode.ROTATING) {
            return Mono.empty();
        }

        StatusPreset preset = ROTATION_PRESETS.get(rotationIndex.getAndUpdate(i -> (i + 1) % ROTATION_PRESETS.size()));

        return fetchStats()
                .map(stats -> preset.createActivity(stats))
                .doOnNext(activity -> LOGGER.info("Rotating presence to: {} - {}", preset.getTypeName(), preset.getName()))
                .flatMap(this::updatePresenceWithActivity)
                .doOnSuccess(v -> LOGGER.debug("Presence updated successfully"))
                .doOnError(e -> LOGGER.error("Failed to update presence", e));
    }

    private Mono<Void> updatePresenceWithActivity(ClientActivity activity) {
        return client.updatePresence(ClientPresence.online(activity));
    }

    private Mono<StatusPreset.BotStats> fetchStats() {
        return client.getGuilds().count()
                .defaultIfEmpty(0L)
                .map(servers -> {
                    try (Connection conn = DatabaseService.getConnection()) {
                        ReadFromSQL reader = new ReadFromSQL(conn);
                        long users = reader.countUsers();
                        long games = reader.countGames();
                        long activitiesToday = reader.countActivitiesToday();
                        return new StatusPreset.BotStats(users, games, activitiesToday, servers);
                    } catch (SQLException e) {
                        LOGGER.error("Failed to fetch stats for presence", e);
                        return new StatusPreset.BotStats(0, 0, 0, servers);
                    }
                });
    }
}
