package com.hellzing.discordchat.commands;

import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.User;

public interface ICommand
{
    enum ChannelType
    {
        PRIVATE, GUILD, BOTH
    }

    enum PermissionType
    {
        OWNER, ADMIN, EVERYONE
    }

    ChannelType getChannelType();

    PermissionType getPermissionType();

    String[] getCommandAliases();

    boolean execute(User sender, MessageChannel channel, String[] args);
}
