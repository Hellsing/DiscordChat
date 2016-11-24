package com.hellzing.discordchat.utils;

import com.hellzing.discordchat.data.Config;
import lombok.val;
import net.dv8tion.jda.entities.TextChannel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class Utility
{
    /**
     * Checks if the provided channel is currently monitored by the mod.
     * @param channel The channel to check.
     * @return Whether or not the channel is monitored.
     */
    public static boolean isChannelMonitored(TextChannel channel)
    {
        for (String s : Config.getInstance().getMonitoredChannels())
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
        // Create a new chat component
        val component = new ChatComponentText(message);

        // Send the string message as Minecraft chat message to all online players
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(component);
    }

    /**
     * Returns the correct Minecraft player name, respecting scoreboard team settings, without color codes.
     * @param player The player.
     * @return The full player name without color codes.
     */
    public static String getPlayerName(EntityPlayer player)
    {
        // Return the formatted Minecraft player name without color codes
        return stripMinecraftColors(ScorePlayerTeam.formatPlayerName(player.getTeam(), player.getDisplayName()));
    }

    /**
     * Removes all color codes from a given text.
     * @param text The text to remove the color codes from.
     * @return The text without the color codes.
     */
    public static String stripMinecraftColors(String text)
    {
        return text.replaceAll("\u00A7([0-9a-fk-orA-FK-OR])", "");
    }
}
