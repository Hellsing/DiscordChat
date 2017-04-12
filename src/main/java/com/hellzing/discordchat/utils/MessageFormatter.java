package com.hellzing.discordchat.utils;

import com.hellzing.discordchat.DiscordChat;
import com.hellzing.discordchat.data.Messages;
import com.hellzing.discordchat.discord.DiscordWrapper;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.entities.Member;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public class MessageFormatter
{
    @Getter
    private static final String newLine = System.getProperty("line.separator");

    private static final Pattern tagPattern = Pattern.compile("@(.+?)\\b");
    private static final Pattern emotePattern = Pattern.compile(":(.+?\\b):");

    public static String getPlayerJoinMessage(String username)
    {
        return Messages.getInstance().getDiscord().getPlayerJoin().format(username);
    }

    public static String getPlayerLeaveMessage(String username)
    {
        return Messages.getInstance().getDiscord().getPlayerLeave().format(username);
    }

    public static String getPlayerAchievementMessage(String username, String achievementName)
    {
        return Messages.getInstance().getDiscord().getPlayerAchievement().format(username, achievementName);
    }

    public static String getPlayerDeathMessage(String deathMessage)
    {
        return Messages.getInstance().getDiscord().getPlayerDeath().format(deathMessage);
    }

    public static String getPlayerBossKilledMessage(String dimensionName, String playerName, String bossName)
    {
        return Messages.getInstance().getDiscord().getPlayerBossKilled().format(dimensionName, playerName, bossName);
    }

    public static String getMinecraftToDiscordMessage(String message)
    {
        // Get the correctly formatted message
        String toSend = Messages.getInstance().getDiscord().getMinecraftChat().format(message);

        try
        {
            // Create a StringBuffer
            StringBuffer buffer = new StringBuffer();

            // Find all tagged users
            val tagMatcher = tagPattern.matcher(toSend);
            while (tagMatcher.find())
            {
                // Get the complete matching user
                Optional<Member> foundUser = DiscordWrapper.getServer().getMembers().stream().filter(user -> user.getUser().getName().equalsIgnoreCase(tagMatcher.group(1))).findFirst();

                // Search further if user was not found
                if (!foundUser.isPresent())
                {
                    foundUser = DiscordWrapper.getServer()
                                              .getMembers()
                                              .stream()
                                              .filter(user -> StringUtils.containsIgnoreCase(user.getUser().getName(), tagMatcher.group(1))).findFirst();

                    // And even further by searching for the nickname
                    if (!foundUser.isPresent())
                    {
                        foundUser = DiscordWrapper.getServer()
                                                  .getMembers()
                                                  .stream()
                                                  .filter(user -> StringUtils.containsIgnoreCase(user.getNickname(), tagMatcher.group(1))).findFirst();
                    }
                }

                if (foundUser.isPresent())
                {
                    // Replace found occurrence with Discord mention
                    tagMatcher.appendReplacement(buffer, foundUser.get().getAsMention());
                }
                else
                {
                    // No error, but list the possible mentionable users as debug message in the logs
                    DiscordChat.getLogger().debug("Possible tagged user not found: " + tagMatcher.group(1));
                    DiscordChat.getLogger().debug("Users: " + Arrays.toString(DiscordWrapper.getServer().getMembers().stream().map(user -> user.getEffectiveName().toLowerCase()).toArray()));
                    tagMatcher.appendReplacement(buffer, "@" + tagMatcher.group(1));
                }
            }

            // Add the remaining message
            tagMatcher.appendTail(buffer);
            toSend = tagMatcher.toString();

            // Find custom emotes
            buffer = new StringBuffer();
            val emoteMatcher = emotePattern.matcher(toSend);
            while (emoteMatcher.find())
            {
                val emotes = DiscordWrapper.getServer().getEmotesByName(emoteMatcher.group(1), true);

                if (emotes.size() > 0)
                {
                    // Replace with the first emote found
                    emoteMatcher.appendReplacement(buffer, emotes.get(0).getAsMention());
                }
            }

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
        return Messages.getInstance().getMinecraft().getDiscordChat().format(username, message);
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
