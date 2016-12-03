package com.hellzing.discordchat.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;

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

    String getDescription();

    boolean execute(Member sender, MessageChannel channel, String[] args);
}
