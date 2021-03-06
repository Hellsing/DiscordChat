package com.hellzing.discordchat.commands;

import com.hellzing.discordchat.utils.MessageFormatter;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.minecraft.server.MinecraftServer;

public class Online implements ICommand
{
    private static final String defaultMessage = "`Server is empty` :dizzy_face:";
    private static final String onlineFormat = "--- Currently Online: %1$d ---";

    @Getter
    private final ChannelType channelType = ChannelType.BOTH;
    @Getter
    private final PermissionType permissionType = PermissionType.EVERYONE;
    @Getter
    private final String[] commandAliases = new String[] { "online", "on", "list" };
    @Getter
    private final String description = "Displays all current players on the server";

    @Override
    public boolean execute(Member sender, MessageChannel channel, String[] args)
    {
        if (MinecraftServer.getServer().getCurrentPlayerCount() == 0)
        {
            // Send default message
            channel.sendMessage(defaultMessage).queue();
        }
        else
        {
            val sb = new StringBuilder();

            // Apply current player count
            sb.append(String.format(onlineFormat, MinecraftServer.getServer().getCurrentPlayerCount()));

            // Check if any player is playing
            if (MinecraftServer.getServer().getCurrentPlayerCount() > 0)
            {
                // Add a new line followed by all user names separated by comma
                sb.append(MessageFormatter.getNewLine());
                sb.append("+ ");
                sb.append(MinecraftServer.getServer().getConfigurationManager().getPlayerNamesString(false));
            }

            // Send message to discord channel
            channel.sendMessage(MessageFormatter.getDiscordCodeBlock("diff", sb.toString())).queue();
        }

        return true;
    }
}
