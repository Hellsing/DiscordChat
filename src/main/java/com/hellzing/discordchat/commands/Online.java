package com.hellzing.discordchat.commands;

import com.hellzing.discordchat.discord.DiscordThread;
import net.dv8tion.jda.entities.User;
import net.minecraft.server.MinecraftServer;

public class Online implements ICommand
{
    private static final String onlineFormat = "!--- Currently Online: %1$d ---!";

    @Override
    public String[] getCommandAliases()
    {
        return new String[] { "online" };
    }

    @Override
    public void doCommand(User sender, String channel, String[] args)
    {
        StringBuilder sb = new StringBuilder();

        // Tag the sender
        sb.append(sender.getAsMention());
        sb.append(System.getProperty("line.separator"));

        // Syntax highlighting
        sb.append("```diff");

        // Apply current player count
        sb.append(String.format(onlineFormat, MinecraftServer.getServer().getCurrentPlayerCount()));

        // Check if any player is playing
        if (MinecraftServer.getServer().getCurrentPlayerCount() > 0)
        {
            // Add a new line followed by all user names separated by comma
            sb.append(System.getProperty("line.separator"));
            sb.append("+ ");
            sb.append(MinecraftServer.getServer().getConfigurationManager().func_152609_b(false));
        }

        // End syntax highlighting
        sb.append("```");

        // Send message to discord channel
        DiscordThread.instance.sendMessageToChannel(channel, sb.toString());
    }
}
