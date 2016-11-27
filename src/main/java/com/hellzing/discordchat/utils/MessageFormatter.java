package com.hellzing.discordchat.utils;

import com.hellzing.discordchat.DiscordChat;
import com.hellzing.discordchat.data.Messages;
import com.hellzing.discordchat.discord.DiscordWrapper;
import lombok.Getter;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Pattern;

public class MessageFormatter
{
    @Getter
    private static final String newLine = System.getProperty("line.separator");
    private static final String defaultSyntax = "diff";

    private static final Pattern tagPattern = Pattern.compile("@(.+?)\\b");

    public static String getPlayerJoinMessage(String username)
    {
        return getDiscordCodeBlock(defaultSyntax, Messages.getInstance().getPlayerJoin().format(username));
    }

    public static String getPlayerLeaveMessage(String username)
    {
        return getDiscordCodeBlock(defaultSyntax, Messages.getInstance().getPlayerLeave().format(username));
    }

    public static String getPlayerAchievementMessage(String username, String achievementName)
    {
        return getDiscordCodeBlock("", Messages.getInstance().getPlayerAchievement().format(username, achievementName));
    }

    public static String getPlayerDeathMessage(String deathMessage)
    {
        return getDiscordCodeBlock(defaultSyntax, Messages.getInstance().getPlayerDeath().format(deathMessage));
    }

    public static String getPlayerBossKilledMessage(String dimensionName, String playerName, String bossName)
    {
        return getDiscordCodeBlock(defaultSyntax, Messages.getInstance().getPlayerBossKilled().format(dimensionName, playerName, bossName));
    }

    public static String getMinecraftToDiscordMessage(String username, String message)
    {
        // Get the correctly formatted message
        String toSend = Messages.getInstance().getMinecraftChat().format(username, message);

        try
        {
            // Create a StringBuffer
            val buffer = new StringBuffer();

            // Find all tagged users
            val matcher = tagPattern.matcher(toSend);
            while (matcher.find())
            {
                val foundUser = DiscordWrapper.getServer()
                                              .getUsers()
                                              .stream()
                                              .filter(user -> StringUtils.containsIgnoreCase(user.getUsername(), matcher.group(1)))
                                              .findFirst();
                if (foundUser.isPresent())
                {
                    matcher.appendReplacement(buffer, foundUser.get().getAsMention());
                }
                else
                {
                    DiscordChat.getLogger().debug("Possible tagged user not found: " + matcher.group(1));
                    DiscordChat.getLogger().debug("Users: " + Arrays.toString(DiscordWrapper.getServer().getUsers().stream().map(user -> user.getUsername().toLowerCase()).toArray()));
                    matcher.appendReplacement(buffer, "@" + matcher.group(1));
                }
            }

            // Add tail
            matcher.appendTail(buffer);

            // Return result
            return buffer.toString();
        }
        catch (Exception e)
        {
            DiscordChat.getLogger().error("An exception occurred while trying to replace mentioned users in message!", e);
        }

        return toSend;
    }

    public static String getDiscordToMinecraftMessage(String username, String message)
    {
        return Messages.getInstance().getDiscordChat().format(username, message);
    }

    public static String getDiscordCodeBlock(String syntax, String text)
    {
        val sb = new StringBuilder();

        // Start Discord code block
        sb.append("```");

        // Add syntax
        sb.append(syntax);

        if (text != null && text.length() > 0)
        {
            // Add a new line for the starting code block
            sb.append(newLine);

            // Add the text
            sb.append(text);
        }

        // End Discord code block
        sb.append(newLine);
        sb.append("```");

        return sb.toString();
    }
}
