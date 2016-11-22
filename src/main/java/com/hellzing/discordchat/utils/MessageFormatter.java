package com.hellzing.discordchat.utils;

import lombok.Getter;
import lombok.val;

public class MessageFormatter
{
    private static final String newLine = System.getProperty("line.separator");
    private static final String defaultSyntax = "diff";

    @Getter
    private static final String playerJoinFormat = "+ %s joined the Minecraft server.";
    @Getter
    private static final String playerLeaveFormat = "- %s left the Minecraft server.";
    @Getter
    private static final String playerAchievementFormat = "%s has just earned the achievement [%s]";
    @Getter
    private static final String playerDeathFormat = "- DEATH -" + newLine + "%s";
    @Getter
    private static final String minecraftChatFormat = "`<%s>` %s";
    @Getter
    private static final String discordChatFormat = "\u00A77DC \u00BB\u00A7r <\u00A73%s\u00A7r> %s";

    public static String getPlayerJoinMessage(String username)
    {
        return getDiscordCodeBlock(defaultSyntax, String.format(playerJoinFormat, username));
    }

    public static String getPlayerLeaveMessage(String username)
    {
        return getDiscordCodeBlock(defaultSyntax, String.format(playerLeaveFormat, username));
    }

    public static String getPlayerAchievementMessage(String username, String achievementName)
    {
        return getDiscordCodeBlock("", String.format(playerAchievementFormat, username, achievementName));
    }

    public static String getPlayerDeathMessage(String deathMessage)
    {
        return getDiscordCodeBlock(defaultSyntax, String.format(playerDeathFormat, deathMessage));
    }

    public static String getMinecraftToDiscordMessage(String username, String message)
    {
        return String.format(minecraftChatFormat, username, message);
    }

    public static String getDiscordToMinecraftMessage(String username, String message)
    {
        return String.format(discordChatFormat, username, message);
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
