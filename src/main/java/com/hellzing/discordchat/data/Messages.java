package com.hellzing.discordchat.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hellzing.discordchat.DiscordChat;
import com.hellzing.discordchat.utils.MessageFormatter;
import lombok.Getter;
import lombok.val;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class Messages
{
    @Getter
    private static final File messagesFile = new File(Config.getConfigFolder().getAbsolutePath() + File.separatorChar + "Messages.json");

    @Getter(lazy = true)
    private static final Messages instance = loadConfigFile();

    @Getter
    @Expose
    @SerializedName("(Discord) Player join message")
    private String playerJoinFormat = "+ %s joined the Minecraft server.";
    @Getter
    @Expose
    @SerializedName("(Discord) Player leave message")
    private String playerLeaveFormat = "- %s left the Minecraft server.";
    @Getter
    @Expose
    @SerializedName("(Discord) Player achievement message")
    private String playerAchievementFormat = "%s has just earned the achievement [%s]";
    @Getter
    @Expose
    @SerializedName("(Discord) Player death message")
    private String playerDeathFormat = "- DEATH -" + MessageFormatter.getNewLine() + "%s";
    @Getter
    @Expose
    @SerializedName("(Discord) Chat from Minecraft")
    private String minecraftChatFormat = "`<%s>` %s";
    @Getter
    @Expose
    @SerializedName("(Minecraft) Chat from Discord")
    private String discordChatFormat = "\u00A77DC \u00BB\u00A7r <\u00A73%s\u00A7r> %s";

    private static Messages loadConfigFile()
    {
        // Create messages instance
        Messages messages = new Messages();

        try
        {
            // Check if a config file exists
            if (!messagesFile.exists())
            {
                // Create file within directory
                messagesFile.createNewFile();
            }

            // Parse the config
            val configJson = FileUtils.readFileToString(messagesFile, "utf-8");
            val parsedConfig = Config.getGson().fromJson(configJson, Messages.class);
            if (parsedConfig != null)
            {
                // Apply parsed config object
                messages = parsedConfig;
            }

            // Save the config back to file
            FileUtils.writeStringToFile(messagesFile, Config.getGson().toJson(messages));
        }
        catch (Exception e)
        {
            DiscordChat.getLogger().error("Failed to parse messages from file", e);
        }

        // Apply instance
        return messages;
    }
}
