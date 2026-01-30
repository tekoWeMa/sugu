package ch.wema.commands;

import ch.wema.core.command.Command;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.InviteCreateSpec;
import discord4j.rest.util.Permission;
import reactor.core.publisher.Mono;

public class InviteCommand implements Command {

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String ownerId = System.getenv("BOT_OWNER_ID");
        String userId = event.getInteraction().getUser().getId().asString();
        if (ownerId == null || !userId.equals(ownerId)) {
            return event.reply().withEphemeral(true).withContent("This command is owner-only.");
        }

        String serverId = event.getOptions().get(0)
                .getOption("server_id")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(val -> val.asString())
                .orElse("");

        if (serverId.isEmpty()) {
            return event.reply().withEphemeral(true).withContent("Please provide a server ID.");
        }

        return event.deferReply().withEphemeral(true)
                .then(event.getClient().getGuildById(Snowflake.of(serverId))
                        .onErrorResume(e -> Mono.empty())
                        .flatMap(guild -> createInviteForGuild(guild, event))
                        .switchIfEmpty(Mono.defer(() -> event.editReply()
                                .withContentOrNull("Could not find server with ID: `" + serverId + "`")
                                .thenReturn("not-found"))))
                .then();
    }

    private Mono<String> createInviteForGuild(Guild guild, ChatInputInteractionEvent event) {
        return guild.getChannels()
                .ofType(TextChannel.class)
                .filterWhen(channel -> channel.getEffectivePermissions(event.getClient().getSelfId())
                        .map(perms -> perms.contains(Permission.CREATE_INSTANT_INVITE)))
                .next()
                .flatMap(channel -> channel.createInvite(InviteCreateSpec.builder()
                                .maxAge(86400)
                                .maxUses(1)
                                .reason("Requested by bot owner")
                                .build())
                        .flatMap(invite -> event.editReply()
                                .withContentOrNull("**" + guild.getName() + "**\n" +
                                        "Invite: https://discord.gg/" + invite.getCode() + "\n" +
                                        "Expires: 24 hours\n" +
                                        "Max uses: 1")
                                .thenReturn("success")))
                .switchIfEmpty(Mono.defer(() -> event.editReply()
                        .withContentOrNull("Could not create invite for **" + guild.getName() + "**\n" +
                                "Bot lacks `CREATE_INSTANT_INVITE` permission in any channel.")
                        .thenReturn("no-permission")));
    }
}
