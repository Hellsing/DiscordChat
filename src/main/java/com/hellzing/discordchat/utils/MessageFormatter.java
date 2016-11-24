package com.hellzing.discordchat.utils;

import com.hellzing.discordchat.data.Messages;
import lombok.Getter;
import lombok.val;

public class MessageFormatter
{
    @Getter
    private static final String newLine = System.getProperty("line.separator");
    private static final String defaultSyntax = "diff";

    public static String getPlayerJoinMessage(String username)
    {
        return getDiscordCodeBlock(defaultSyntax, String.format(Messages.getInstance().getPlayerJoinFormat(), username));
    }

    public static String getPlayerLeaveMessage(String username)
    {
        return getDiscordCodeBlock(defaultSyntax, String.format(Messages.getInstance().getPlayerLeaveFormat(), username));
    }

    public static String getPlayerAchievementMessage(String username, String achievementName)
    {
        return getDiscordCodeBlock("", String.format(Messages.getInstance().getPlayerAchievementFormat(), username, achievementName));
    }

    public static String getPlayerDeathMessage(String deathMessage)
    {
        return getDiscordCodeBlock(defaultSyntax, String.format(Messages.getInstance().getPlayerDeathFormat(), deathMessage));
    }

    public static String getMinecraftToDiscordMessage(String username, String message)
    {
        return String.format(Messages.getInstance().getMinecraftChatFormat(), username, message);
    }

    public static String getDiscordToMinecraftMessage(String username, String message)
    {
        return String.format(Messages.getInstance().getDiscordChatFormat(), username, message);
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
