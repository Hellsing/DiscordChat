package com.hellzing.discordchat.commands;

import net.dv8tion.jda.entities.User;

public interface ICommand
{
    String[] getCommandAliases();

    void doCommand(User sender, String channel, String[] args);
}
