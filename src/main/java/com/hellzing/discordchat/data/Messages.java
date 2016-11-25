package com.hellzing.discordchat.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hellzing.discordchat.utils.MessageFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Messages
{
    @Getter
    private static final File messagesFile = new File(Config.getConfigFolder().getAbsolutePath() + File.separatorChar + "Messages.json");

    @Getter
    private static Messages instance;

    @NoArgsConstructor
    @AllArgsConstructor
    public class Message extends ToggleableBase
    {
        @Getter
        @Expose
        @SerializedName("Format")
        protected String messageFormat = "";

        /**
         * Formats the message just like String.format()
         * @param args The args to use for the format string.
         * @return The formatted string.
         */
        public String format(Object... args)
        {
            return String.format(messageFormat, args);
        }
    }

    @Getter
    @Expose
    @SerializedName("(Discord) Player joins Minecraft server")
    private Message playerJoin = new Message("+ %s joined the Minecraft server.");

    @Getter
    @Expose
    @SerializedName("(Discord) Player leaves Minecraft server")
    private Message playerLeave = new Message("- %s left the Minecraft server.");

    @Getter
    @Expose
    @SerializedName("(Discord) Player got achievement")
    private Message playerAchievement = new Message("%s has just earned the achievement [%s]");

    @Getter
    @Expose
    @SerializedName("(Discord) Player death")
    private Message playerDeath = new Message("- DEATH -" + MessageFormatter.getNewLine() + "%s");

    @Getter
    @Expose
    @SerializedName("(Discord) Player killed a boss monster")
    private Message playerBossKilled = new Message("+ Boss killed in dimension: %s!" + MessageFormatter.getNewLine() + "%s has slain the %s!");

    @Getter
    @Expose
    @SerializedName("(Discord) Chat from Minecraft")
    private Message minecraftChat = new Message("`<%s>` %s");

    @Getter
    @Expose
    @SerializedName("(Minecraft) Chat from Discord")
    private Message discordChat = new Message("\u00A77DC \u00BB\u00A7r <\u00A73%s\u00A7r> %s");

    private static Messages loadConfigFile() throws IOException
    {
        // Create messages instance
        Messages messages = new Messages();

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
        FileUtils.writeStringToFile(messagesFile, Config.getGson().toJson(messages, Messages.class));

        // Apply instance
        return messages;
    }

    public static void reloadConfig() throws IOException
    {
        // Load the config file
        instance = loadConfigFile();
    }
}
