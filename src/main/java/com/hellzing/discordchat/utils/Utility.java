package com.hellzing.discordchat.utils;

import com.hellzing.discordchat.DCConfig;
import net.dv8tion.jda.entities.TextChannel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StringUtils;

public class Utility
{
    /**
     * Checks if the provided channel is currently monitored by the mod.
     * @param channel The channel to check.
     * @return Whether or not the channel is monitored.
     */
    public static boolean isChannelMonitored(TextChannel channel)
    {
        for (String s : DCConfig.channels)
        {
            if (channel.getName().equalsIgnoreCase(s))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Sends a message to the Minecraft server, visible for all players.
     * @param message The message to send.
     */
    public static void sendMinecraftChat(String message)
    {
        // Send the string message as Minecraft chat message to all online players
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText(message));
    }

    /**
     * Returns the correct Minecraft player name, respecting scoreboard team settings, without color codes.
     * @param player The player.
     * @return The full player name without color codes.
     */
    public static String getPlayerName(EntityPlayer player)
    {
        // Return the formatted Minecraft player name without color codes
        return StringUtils.stripControlCodes(ScorePlayerTeam.formatPlayerName(player.getTeam(), player.getDisplayName()));
    }
}
