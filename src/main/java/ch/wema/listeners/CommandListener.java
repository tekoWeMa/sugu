package ch.wema.listeners;

import ch.wema.commands.Command;
import ch.wema.commands.PingCommand;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class CommandListener {

    private final static List<Command> commands = new ArrayList<>();

    static {
        commands.add(new PingCommand());
    }

    public static Mono<Void> handle(ChatInputInteractionEvent event) {

        System.out.println(event.getCommandName());

        return Flux.fromIterable(commands)
                .filter(command -> command.getName().equals(event.getCommandName()))
                .next()
                .flatMap(command -> command.handle(event));
    }
}
