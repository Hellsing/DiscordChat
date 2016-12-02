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

public final class Messages
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

    @NoArgsConstructor
    @AllArgsConstructor
    public final class DiscordMessage extends Message
    {
        public static final String defaultSyntax = "diff";

        @NoArgsConstructor
        @AllArgsConstructor
        public final class Style
        {
            @Getter
            @Expose
            @SerializedName("Use code block")
            private boolean codeBlock = false;
            @Getter
            @Expose
            @SerializedName("Code syntax")
            private String codeSyntax = "";

            /**
             * Wraps the given text in a Discord code block, using the given code syntax from the object. This method ignores whether the codeBlock value is set or not.
             * @param text The text to wrap in a Discord code block.
             * @return The wrapped code block text.
             */
            public String apply(String text)
            {
                return MessageFormatter.getDiscordCodeBlock(codeSyntax, text);
            }
        }

        @Getter
        @Expose
        @SerializedName("Message style")
        private Style style = new Style();

        @Override
        public String format(Object... args)
        {
            // Get formatted message from the overridden method
            String formattedMessage = super.format(args);

            // Check if code block needs to be applied
            if (style.codeBlock)
            {
                // Apply code block
                formattedMessage = style.apply(formattedMessage);
            }

            return formattedMessage;
        }

        public DiscordMessage(String messageFormat)
        {
            super(messageFormat);
        }

        /**
         * Creates a new instance of DiscordMessage, setting code block style to true and applying the given code syntax.
         * @param messageFormat The message format.
         * @param codeSyntax The preferred code syntax.
         */
        public DiscordMessage(String messageFormat, String codeSyntax)
        {
            this(messageFormat);
            this.style.codeBlock = true;
            this.style.codeSyntax = codeSyntax;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public final class Discord
    {
        @Getter
        @Expose
        @SerializedName("Chat from Minecraft")
        private DiscordMessage minecraftChat = new DiscordMessage("`<%s>` %s");

        @Getter
        @Expose
        @SerializedName("Server has started")
        private DiscordMessage serverStarted = new DiscordMessage("`Server has successfully started and you are able to join!` :ok_hand:");

        @Getter
        @Expose
        @SerializedName("Server is stopping")
        private DiscordMessage serverStopping = new DiscordMessage("`Server is shutting down!` :sleeping:");

        @Getter
        @Expose
        @SerializedName("Player joins Minecraft server")
        private DiscordMessage playerJoin = new DiscordMessage("+ %s joined the Minecraft server.", DiscordMessage.defaultSyntax);

        @Getter
        @Expose
        @SerializedName("Player leaves Minecraft server")
        private DiscordMessage playerLeave = new DiscordMessage("- %s left the Minecraft server.", DiscordMessage.defaultSyntax);

        @Getter
        @Expose
        @SerializedName("Player got achievement")
        private DiscordMessage playerAchievement = new DiscordMessage("%s has just earned the achievement [%s]", "");

        @Getter
        @Expose
        @SerializedName("Player death")
        private DiscordMessage playerDeath = new DiscordMessage("- DEATH -" + MessageFormatter.getNewLine() + "%s", DiscordMessage.defaultSyntax);

        @Getter
        @Expose
        @SerializedName("Player killed a boss monster")
        private DiscordMessage playerBossKilled = new DiscordMessage("+ Boss killed in dimension: %s!" + MessageFormatter.getNewLine() + "%s has slain the %s!", DiscordMessage.defaultSyntax);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public class Minecraft
    {
        @Getter
        @Expose
        @SerializedName("Chat from Discord")
        private Message discordChat = new Message("\u00A77DC \u00BB\u00A7r <\u00A73%s\u00A7r> %s");
    }

    @Getter
    @Expose
    @SerializedName("Discord")
    private Discord discord = new Discord();

    @Getter
    @Expose
    @SerializedName("Minecraft")
    private Minecraft minecraft = new Minecraft();

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
