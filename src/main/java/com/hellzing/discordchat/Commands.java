package com.hellzing.discordchat;

import com.hellzing.discordchat.commands.ICommand;
import lombok.Getter;
import lombok.val;

import java.util.Arrays;
import java.util.HashSet;

public class Commands
{
    @Getter
    private static final Commands instance = new Commands();

    private HashSet<ICommand> commands = new HashSet<>();

    // Make constructor private to prevent multiple instantiations
    private Commands()
    {
    }

    /**
     * Get a command matching the name.
     * @param name The name to match.
     * @return The found command or null when not found.
     */
    public ICommand getCommand(String name)
    {
        for (val command : commands)
        {
            if (Arrays.asList(command.getCommandAliases()).contains(name.toLowerCase()))
            {
                return command;
            }
        }
        return null;
    }

    /**
     * Registers a new command handler.
     * @param instance The command instance to register.
     */
    public void registerCommand(ICommand instance)
    {
        commands.add(instance);
    }
}
