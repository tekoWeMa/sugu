package ch.wema.core.command;

import ch.wema.core.reaction.Reaction;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

public interface Command extends Reaction<ChatInputInteractionEvent> {
    String getName();
}
