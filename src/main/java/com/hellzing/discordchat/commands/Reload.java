package com.hellzing.discordchat.commands;

import lombok.Getter;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.User;

public class Reload implements ICommand
{
    @Getter
    private ICommand.ChannelType channelType = ChannelType.PRIVATE;
    @Getter
    private ICommand.PermissionType permissionType = PermissionType.OWNER;
    @Getter
    private String[] commandAliases = new String[] { "reload", "rl" };

    @Override
    public boolean doCommand(User sender, MessageChannel channel, String[] args)
    {
        channel.sendMessage("Reloaded!");
        return true;
    }
}
