package ch.wema.reactions;

import ch.wema.core.reaction.Reaction;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.PresenceUpdateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.Activity;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public class StalkReaction implements Reaction<PresenceUpdateEvent> {
    @Override
    public Mono<Void> handle(PresenceUpdateEvent event) {
        //check if this is even getting used
        return Mono.just(event)
                .flatMap(e -> {
                    String userId = e.getUserId().asString();
                    var client = e.getClient();
                    var user = client.getUserById(Snowflake.of(userId));
                    return user.flatMap(u -> {
                        // Extract the user's status from their status
                        String status = event.getCurrent().getStatus().toString();

                        // Build the base log message
                        StringBuilder content = new StringBuilder();
                        content.append("The Status of the user ")
                               .append(u.getUsername())
                               .append(" (").append(u.getId().asString()).append(") ")
                               .append("changed to ").append(status).append(".");

                        // If the user has any activities, append them to the log message
                        List<Activity> activities = event.getCurrent().getActivities();
                        if (!activities.isEmpty()) {
                            content.append("\nActivities:");
                            for (Activity activity : activities) {
                                content.append("\n- Name: ").append(activity.getName())
                                       .append("\n  Type: ").append(activity.getType().name());

                                appendIfPresent(content, "Details", activity.getDetails());
                                appendIfPresent(content, "State", activity.getState());
                                appendIfPresent(content, "Start", activity.getStart());
                                appendIfPresent(content, "End", activity.getEnd());
                                appendIfPresent(content, "Application ID", activity.getApplicationId().map(Snowflake::asString));
                            }
                        }

                        return ((MessageChannel) client.getChannelById(Snowflake.of("1008364168753193030")).block()).createMessage(content.toString());
                    }).then();

                });
    }

    private <T> void appendIfPresent(StringBuilder sb, String label, Optional<T> value) {
        value.ifPresent(v -> sb.append("\n  ").append(label).append(": ").append(v));
    }
}
