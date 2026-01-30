package ch.wema.commands;

import ch.wema.core.command.Command;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Guild;
import reactor.core.publisher.Mono;

public class ServersCommand implements Command {

    @Override
    public String getName() {
        return "servers";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String ownerId = System.getenv("BOT_OWNER_ID");
        String userId = event.getInteraction().getUser().getId().asString();
        if (ownerId == null || !userId.equals(ownerId)) {
            return event.reply().withEphemeral(true).withContent("This command is owner-only.");
        }

        return event.deferReply().withEphemeral(true)
                .then(event.getClient().getGuilds()
                        .collectList()
                        .flatMap(guilds -> {
                            if (guilds.isEmpty()) {
                                return event.editReply().withContentOrNull("Bot is not in any servers.");
                            }

                            StringBuilder sb = new StringBuilder();
                            sb.append("**Servers (").append(guilds.size()).append("):**\n\n");

                            for (Guild guild : guilds) {
                                sb.append("**").append(guild.getName()).append("**\n");
                                sb.append("  ID: `").append(guild.getId().asString()).append("`\n");
                                sb.append("  Members: ").append(guild.getMemberCount()).append("\n");
                                sb.append("  Owner: <@").append(guild.getOwnerId().asString()).append(">\n");
                                guild.getVanityUrlCode().ifPresent(code ->
                                    sb.append("  Vanity URL: discord.gg/").append(code).append("\n"));
                                sb.append("\n");
                            }

                            String content = sb.toString();
                            if (content.length() > 2000) {
                                content = content.substring(0, 1997) + "...";
                            }

                            return event.editReply().withContentOrNull(content);
                        }))
                .then();
    }
}
