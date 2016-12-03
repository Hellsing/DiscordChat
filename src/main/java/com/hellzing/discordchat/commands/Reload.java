package com.hellzing.discordchat.commands;

import com.hellzing.discordchat.DiscordChat;
import com.hellzing.discordchat.utils.MessageFormatter;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
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
    public boolean execute(Member sender, MessageChannel channel, String[] args)
    {
        channel.sendMessage("Reloading...").queue();
        try
        {
            // Do the actual reloading
            DiscordChat.reloadConfigs();

            channel.sendMessage("Reloading finished without errors!").queue();
        }
        catch (Exception e)
        {
            // Show stacktrace to the user as he is the owner of the bot
            channel.sendMessage("Failed to reload! Here is the exception:" + MessageFormatter.getNewLine() + ExceptionUtils.getStackTrace(e)).queue();
        }
        return true;
    }
}
