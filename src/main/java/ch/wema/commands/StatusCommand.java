package ch.wema.commands;

import ch.wema.core.command.Command;
import ch.wema.presence.BotPresenceManager;
import ch.wema.presence.StatusPreset;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.presence.ClientActivity;
import reactor.core.publisher.Mono;

public class StatusCommand implements Command {

    private final BotPresenceManager presenceManager;

    public StatusCommand(BotPresenceManager presenceManager) {
        this.presenceManager = presenceManager;
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        String ownerId = System.getenv("BOT_OWNER_ID");
        String userId = event.getInteraction().getUser().getId().asString();
        if (ownerId == null || !userId.equals(ownerId)) {
            return event.reply().withEphemeral(true).withContent("This command is owner-only.");
        }

        String subcommand = event.getOptions().get(0).getName();

        return switch (subcommand) {
            case "rotate" -> handleRotate(event);
            case "preset" -> handlePreset(event);
            case "custom" -> handleCustom(event);
            case "show" -> handleShow(event);
            default -> event.reply().withEphemeral(true).withContent("Unknown subcommand");
        };
    }

    private Mono<Void> handleRotate(ChatInputInteractionEvent event) {
        return event.reply()
                .withEphemeral(true)
                .withContent("Rotating status enabled. Status will cycle every 5 minutes.")
                .then(presenceManager.setRotating());
    }

    private Mono<Void> handlePreset(ChatInputInteractionEvent event) {
        String presetName = event.getOptions().get(0)
                .getOption("name")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(val -> val.asString())
                .orElse("");

        StatusPreset preset = StatusPreset.fromName(presetName);
        if (preset == null) {
            return event.reply()
                    .withEphemeral(true)
                    .withContent("Unknown preset: " + presetName);
        }

        return event.reply()
                .withEphemeral(true)
                .withContent("Status set to preset: **" + preset.getName() + "** (" + preset.getTypeName() + ")")
                .then(presenceManager.setPreset(preset));
    }

    private Mono<Void> handleCustom(ChatInputInteractionEvent event) {
        ApplicationCommandInteractionOption customOption = event.getOptions().get(0);

        String type = customOption.getOption("type")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(val -> val.asString())
                .orElse("playing");

        String text = customOption.getOption("text")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(val -> val.asString())
                .orElse("something");

        ClientActivity activity = switch (type) {
            case "listening" -> ClientActivity.listening(text);
            case "watching" -> ClientActivity.watching(text);
            case "competing" -> ClientActivity.competing(text);
            default -> ClientActivity.playing(text);
        };

        String typeDisplay = switch (type) {
            case "listening" -> "Listening to";
            case "watching" -> "Watching";
            case "competing" -> "Competing in";
            default -> "Playing";
        };

        return event.reply()
                .withEphemeral(true)
                .withContent("Custom status set: **" + typeDisplay + "** " + text)
                .then(presenceManager.setCustom(activity));
    }

    private Mono<Void> handleShow(ChatInputInteractionEvent event) {
        String description = presenceManager.getCurrentStatusDescription();
        return event.reply()
                .withEphemeral(true)
                .withContent("Current status mode: **" + description + "**");
    }
}
