package com.hellzing.discordchat.commands;

import com.hellzing.discordchat.discord.DiscordThread;
import net.minecraft.server.MinecraftServer;

public class Online implements ICommand
{
    private static final String onlineFormat = "--- Currently Online: %1$d ---";

    @Override
    public String[] getNames()
    {
        return new String[] { "online" };
    }

    @Override
    public void doCommand(String channel, String[] args)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(onlineFormat, MinecraftServer.getServer().getCurrentPlayerCount()));

        if (MinecraftServer.getServer().getCurrentPlayerCount() > 0)
        {
            sb.append(System.getProperty("line.separator"));
            sb.append(MinecraftServer.getServer().getConfigurationManager().func_152609_b(false));
        }

        DiscordThread.instance.sendMessageToChannel(channel, sb.toString());
    }
}
