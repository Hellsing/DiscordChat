package com.hellzing.discordchat.commands;

import com.hellzing.discordchat.discord.DiscordWrapper;
import net.dv8tion.jda.entities.User;
import net.minecraft.server.MinecraftServer;
import lombok.val;

public class Online implements ICommand
{
    private static final String onlineFormat = "--- Currently Online: %1$d ---";
    private static final String newLine = System.getProperty("line.separator");

    @Override
    public String[] getCommandAliases()
    {
        return new String[] { "online" };
    }

    @Override
    public void doCommand(User sender, String channel, String[] args)
    {
        val sb = new StringBuilder();

        // Syntax highlighting
        sb.append("```");
        sb.append("diff");
        sb.append(newLine);

        // Apply current player count
        sb.append(String.format(onlineFormat, MinecraftServer.getServer().getCurrentPlayerCount()));

        // Check if any player is playing
        if (MinecraftServer.getServer().getCurrentPlayerCount() > 0)
        {
            // Add a new line followed by all user names separated by comma
            sb.append(newLine);
            sb.append("+ ");
            sb.append(MinecraftServer.getServer().getConfigurationManager().func_152609_b(false));
        }

        // End syntax highlighting
        sb.append("```");

        // Send message to discord channel
        DiscordWrapper.getInstance().sendMessageToChannel(channel, sb.toString());
    }
}
