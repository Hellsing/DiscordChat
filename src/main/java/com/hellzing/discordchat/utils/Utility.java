package com.hellzing.discordchat.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.hellzing.discordchat.DiscordChat;
import com.hellzing.discordchat.data.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import net.dv8tion.jda.entities.TextChannel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public static final class Emoji
    {
        @NoArgsConstructor
        @AllArgsConstructor
        public class EmojiData
        {
            @Getter
            @Expose
            @SerializedName("emoji")
            private String emoji;
            @Getter
            @Expose
            @SerializedName("description")
            private String description;
            @Getter
            @Expose
            @SerializedName("category")
            private String category;
            @Getter
            @Expose
            @SerializedName("aliases")
            private List<String> aliases;
        }

        private static final Pattern emojiPattern;

        @Getter
        private static final Set<EmojiData> emojis;

        static
        {
            // Initialize emoji set and pattern
            emojis = new HashSet<>();
            emojiPattern = Pattern.compile("([\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff])");

            try
            {
                // Parse all emoji from the file
                emojis.addAll(readJsonStream(DiscordChat.getResourceAsStream("emoji.json")));

                // Remove invalid emojis
                val iterator = emojis.iterator();
                while (iterator.hasNext())
                {
                    val emoji = iterator.next();
                    if (emoji.emoji == null)
                    {
                        DiscordChat.getLogger().warn("Found null emoji in database, removing it...");
                        iterator.remove();
                    }
                }

                DiscordChat.getLogger().info(String.format("Loaded a total of %1$d emojis into the cache!", emojis.size()));

                DiscordChat.getLogger().debug("Testing all emojis with pattern...");
                int i = 0;
                for (val emoji : emojis)
                {
                    if (emojiPattern.matcher(emoji.emoji).matches())
                    {
                        i++;
                    }
                }
                DiscordChat.getLogger().debug(i + " of them can be parsed!");
            }
            catch (Exception e)
            {
                DiscordChat.getLogger().error("Failed to read emoji data from resources!", e);
            }
        }

        // Source: https://sites.google.com/site/gson/streaming
        private static Set<EmojiData> readJsonStream(InputStream in) throws IOException
        {
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            Set<EmojiData> messages = new HashSet<>();
            reader.beginArray();
            while (reader.hasNext())
            {
                EmojiData message = Config.getGson().fromJson(reader, EmojiData.class);
                messages.add(message);
            }
            reader.endArray();
            reader.close();

            return messages;
        }

        /**
         * Replaces all emoji chars with their corresponding name alias, like :ok_hand:.
         * @param text The text to be parsed for emojis.
         * @return The text with emoji chars replaced to their corresponding names.
         */
        public static String replaceEmojiWithName(String text)
        {
            DiscordChat.getLogger().debug("Processing text to replace emojis: " + text);
            DiscordChat.getLogger().debug("Pattern: " + emojiPattern.toString());

            // Create a buffer to store the text
            val buffer = new StringBuffer();

            // Find all emoji in the given text
            val matcher = emojiPattern.matcher(text);
            while (matcher.find())
            {
                DiscordChat.getLogger().debug("Pattern found emoji, investigating...");

                // Get the emoji
                val emoji = matcher.group(1);

                // Get the matching emoji from the database
                val match = emojis.stream().filter(emojiData -> emojiData.emoji.equals(emoji)).findFirst();
                if (match.isPresent())
                {
                    DiscordChat.getLogger().debug("Emoji has been identified: " + match.get().description);

                    // Append replacement emoji text
                    matcher.appendReplacement(buffer, ":" + match.get().aliases.get(0) + ":");
                }
                else
                {
                    // Emoji not in the database or not found by the regex
                    matcher.appendReplacement(buffer, ":unkown_emoji:");
                }
            }

            // Add the tail
            matcher.appendTail(buffer);

            // Return replaced text
            return buffer.toString();
        }

        // Make constructor private
        private Emoji()
        {
        }
    }
}
