package ch.wema.presence;

import discord4j.core.object.presence.ClientActivity;

import java.util.function.Function;

public enum StatusPreset {
    STATS("stats", "Watching", stats -> ClientActivity.watching(stats.users() + " users • " + stats.games() + " games")),
    ACTIVITIES_TODAY("activities", "Watching", stats -> ClientActivity.watching(stats.activitiesToday() + " activities today")),
    USERS("users", "Watching", stats -> ClientActivity.watching("over " + stats.users() + " users")),
    SERVERS("servers", "Playing", stats -> ClientActivity.playing("in " + stats.servers() + " servers")),
    HELP("help", "Playing", stats -> ClientActivity.playing("/help")),
    LISTENING("listening", "Listening", stats -> ClientActivity.listening("Listening to your Spotify")),
    INVITE("invite", "Playing", stats -> ClientActivity.playing("Add me to Server to Track Stats!"));

    private final String name;
    private final String typeName;
    private final Function<BotStats, ClientActivity> activityFactory;

    StatusPreset(String name, String typeName, Function<BotStats, ClientActivity> activityFactory) {
        this.name = name;
        this.typeName = typeName;
        this.activityFactory = activityFactory;
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public ClientActivity createActivity(BotStats stats) {
        return activityFactory.apply(stats);
    }

    public static StatusPreset fromName(String name) {
        for (StatusPreset preset : values()) {
            if (preset.name.equalsIgnoreCase(name)) {
                return preset;
            }
        }
        return null;
    }

    public record BotStats(long users, long games, long activitiesToday, long servers) {}
}
