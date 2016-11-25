package com.hellzing.discordchat.commands;

import com.hellzing.discordchat.DiscordChat;
import lombok.Getter;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.User;

public class Reload implements ICommand
{
    @Getter
    private final ICommand.ChannelType channelType = ChannelType.PRIVATE;
    @Getter
    private final ICommand.PermissionType permissionType = PermissionType.OWNER;
    @Getter
    private final String[] commandAliases = new String[] { "reload", "rl" };
    @Getter
    private final String description = "Reloads the complete " + DiscordChat.modId + "mod";

    @Override
    public boolean execute(User sender, MessageChannel channel, String[] args)
    {
        channel.sendMessage("Reloaded!");
        return true;
    }
}
