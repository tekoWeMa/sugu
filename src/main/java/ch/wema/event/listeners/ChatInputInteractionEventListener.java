package ch.wema.event.listeners;

import ch.wema.commands.PingCommand;
import ch.wema.commands.StatusCommand;
import ch.wema.core.command.Command;
import ch.wema.presence.BotPresenceManager;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class ChatInputInteractionEventListener {

    private static final List<Command> commands = new ArrayList<>();

    public static void initialize(BotPresenceManager presenceManager) {
        commands.clear();
        commands.add(new PingCommand());
        commands.add(new StatusCommand(presenceManager));
    }

    public static Mono<Void> handle(ChatInputInteractionEvent event) {
        return Flux.fromIterable(commands)
                .filter(command -> command.getName().equals(event.getCommandName()))
                .next()
                .flatMap(command -> command.handle(event));
    }
}
