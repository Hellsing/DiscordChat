package com.hellzing.discordchat.commands;

import com.hellzing.discordchat.DiscordChat;
import com.hellzing.discordchat.utils.MessageFormatter;
import lombok.Getter;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.User;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class Reload implements ICommand
{
    @Getter
    private final ICommand.ChannelType channelType = ChannelType.PRIVATE;
    @Getter
    private final ICommand.PermissionType permissionType = PermissionType.OWNER;
    @Getter
    private final String[] commandAliases = new String[] { "reload", "rl" };
    @Getter
    private final String description = "Reloads all configs";

    @Override
    public boolean execute(User sender, MessageChannel channel, String[] args)
    {
        channel.sendMessage("Reloading...");
        try
        {
            // Do the actual reloading
            DiscordChat.reloadConfigs();

            channel.sendMessage("Reloading finished without errors!");
        }
        catch (Exception e)
        {
            // Show stacktrace to the user as he is the owner of the bot
            channel.sendMessage("Failed to reload! Here is the exception:" + MessageFormatter.getNewLine() + ExceptionUtils.getStackTrace(e));
        }
        return true;
    }
}
