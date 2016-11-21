package com.hellzing.discordchat;

import com.hellzing.discordchat.commands.ICommand;
import lombok.val;

import java.util.Arrays;
import java.util.HashSet;

public class DCCommands
{
    private static DCCommands instance = null;
    private HashSet<ICommand> commands = new HashSet<>();

    private DCCommands()
    {
    }

    public static DCCommands getInstance()
    {
        if (instance == null)
        {
            instance = new DCCommands();
        }
        return instance;
    }

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

    public void registerCommand(ICommand instance)
    {
        commands.add(instance);
    }
}
