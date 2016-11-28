package com.hellzing.discordchat.utils;

import com.hellzing.discordchat.data.Config;
import com.vdurmont.emoji.EmojiParser;
import lombok.val;
import net.dv8tion.jda.core.entities.TextChannel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeHooks;

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
     * Sends a message to the Minecraft server, visible for all players. URLs will be shown as clickable links ingame.
     * @param message The message to send.
     */
    public static void sendMinecraftChat(String message)
    {
        // Create a new chat component, supporting url links
        val component = ForgeHooks.newChatWithLinks(message);

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

    /**
     * Parses all found emojis in the text to their aliases.
     * @param text The text to be parsed.
     * @return The parsed text.
     */
    public static String parseEmojisToAliases(String text)
    {
        return EmojiParser.parseToAliases(text);
    }
}
