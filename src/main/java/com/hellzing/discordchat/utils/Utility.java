package com.hellzing.discordchat.utils;

import com.hellzing.discordchat.DiscordChat;
import com.hellzing.discordchat.data.Config;
import com.hellzing.discordchat.discord.DiscordWrapper;
import com.vdurmont.emoji.EmojiParser;
import lombok.val;
import net.dv8tion.jda.core.entities.TextChannel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility
{
    /**
     * Checks if the provided channel is currently monitored by the mod.
     * @param channel The channel to check.
     * @return Whether or not the channel is monitored.
     */
    public static boolean isChannelMonitored(TextChannel channel)
    {
        for (val monitoredChannel : Config.getInstance().getMonitoredChannels())
        {
            if (channel.getName().equalsIgnoreCase(monitoredChannel))
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
        sendMinecraftChat(message, true);
    }

    /**
     * Sends a message to the Minecraft server, visible for all players.
     * @param message The message to send.
     * @param parseUrls Whether to parse urls into clickable links or not.
     */
    public static void sendMinecraftChat(String message, boolean parseUrls)
    {
        IChatComponent component = new ChatComponentText(message);
        if (parseUrls)
        {
            // Create a new chat component, supporting url links
            component = parseChatLinks(message);
        }

        // Send the string message as Minecraft chat message to all online players
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(component);
    }

    /**
     * Sends a message to the Minecraft server, visible for all players.
     * @param message The message to send.
     */
    public static void sendMinecraftChat(IChatComponent message)
    {
        // Send the string message as Minecraft chat message to all online players
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(message);
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

    /**
     * Validates all channels specified by the config file and logs those which do not match any channel on the server.
     */
    public static void validateMonitoredChannels()
    {
        val channels = DiscordWrapper.getServer().getTextChannels();
        for (val monitoredChannel : Config.getInstance().getMonitoredChannels())
        {
            if (channels.stream().noneMatch(textChannel -> textChannel.getName().equalsIgnoreCase(monitoredChannel)))
            {
                DiscordChat.getLogger().info("Monitored channel not present, check your config! Channel name: " + monitoredChannel);
            }
        }
    }

    static final Pattern URL_PATTERN = Pattern.compile(
            //         schema                          ipv4            OR        namespace                 port     path         ends
            //   |-----------------|        |-------------------------|  |-------------------------|    |---------| |--|   |---------------|
            "((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))",
            Pattern.CASE_INSENSITIVE);

    /**
     * Parses all links in the input string and creates a new ChatComponent with clickable links.
     * @param string The string to parse.
     * @return The IChatComponent instance with clickable links.
     */
    public static IChatComponent parseChatLinks(String string)
    {
        return parseChatLinks(string, true);
    }

    /**
     * Parses all links in the input string and creates a new ChatComponent with clickable links.
     * @param string The string to parse.
     * @param allowMissingHeader Set to allow missing headers or not.
     * @return The IChatComponent instance with clickable links.
     */
    public static IChatComponent parseChatLinks(String string, boolean allowMissingHeader)
    {
        // Includes ipv4 and domain pattern
        // Matches an ip (xx.xxx.xx.xxx) or a domain (something.com) with or
        // without a protocol or path.
        IChatComponent ichat = null;
        Matcher matcher = URL_PATTERN.matcher(string);
        int lastEnd = 0;

        // Find all urls
        while (matcher.find())
        {
            int start = matcher.start();
            int end = matcher.end();

            // Append the previous left overs.
            String part = string.substring(lastEnd, start);
            if (part.length() > 0)
            {
                if (ichat == null)
                {
                    ichat = new ChatComponentText(part);
                }
                else
                {
                    ichat.appendText(part);
                }
            }
            lastEnd = end;
            String url = string.substring(start, end);

            // Get a short version of the link if needed
            String displayedUrl = url;
            if (displayedUrl.length() > 30)
            {
                displayedUrl = url.substring(0, 27) + "...";
            }
            IChatComponent link = new ChatComponentText("[ " + displayedUrl + " ]");

            try
            {
                // Add schema so client doesn't crash.
                if ((new URI(url)).getScheme() == null)
                {
                    if (!allowMissingHeader)
                    {
                        if (ichat == null)
                        {
                            ichat = new ChatComponentText(url);
                        }
                        else
                        {
                            ichat.appendText(url);
                        }
                        continue;
                    }
                    url = "http://" + url;
                }
            }
            catch (URISyntaxException e)
            {
                // Bad syntax bail out!
                if (ichat == null)
                {
                    ichat = new ChatComponentText(url);
                }
                else
                {
                    ichat.appendText(url);
                }
                continue;
            }

            // Set the click event and append the link.
            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
            link.getChatStyle().setChatClickEvent(click);
            HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(url));
            link.getChatStyle().setChatHoverEvent(hover);
            link.getChatStyle().setColor(EnumChatFormatting.GOLD);
            if (ichat == null)
            {
                ichat = link;
            }
            else
            {
                ichat.appendSibling(link);
            }
        }

        // Append the rest of the message.
        String end = string.substring(lastEnd);
        if (ichat == null)
        {
            ichat = new ChatComponentText(end);
        }
        else if (end.length() > 0)
        {
            ichat.appendText(string.substring(lastEnd));
        }
        return ichat;
    }
}
