package ch.wema.reactions;

import ch.wema.core.reaction.Reaction;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class GuildJoinReaction implements Reaction<GuildCreateEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuildJoinReaction.class);
    private static final String LOG_CHANNEL_ID = "1008364168753193030";

    @Override
    public Mono<Void> handle(GuildCreateEvent event) {
        Guild guild = event.getGuild();
        String ownerId = System.getenv("BOT_OWNER_ID");

        StringBuilder message = new StringBuilder();
        message.append("<@").append(ownerId).append("> Bot joined a new server!\n\n");
        message.append("**").append(guild.getName()).append("**\n");
        message.append("ID: `").append(guild.getId().asString()).append("`\n");
        message.append("Members: ").append(guild.getMemberCount()).append("\n");
        message.append("Owner: <@").append(guild.getOwnerId().asString()).append(">\n");

        LOGGER.info("Bot joined server: {} ({})", guild.getName(), guild.getId().asString());

        return event.getClient()
                .getChannelById(Snowflake.of(LOG_CHANNEL_ID))
                .ofType(MessageChannel.class)
                .flatMap(channel -> channel.createMessage(message.toString()))
                .then();
    }
}
