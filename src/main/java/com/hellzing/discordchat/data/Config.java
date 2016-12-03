package com.hellzing.discordchat.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hellzing.discordchat.DiscordChat;
import lombok.Getter;
import lombok.val;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Config
{
    // Resulting file: Minecraft\config\DiscordChat\DiscordChat.json
    @Getter
    private static final File configFile = new File(DiscordChat.getModConfigDirectory().getAbsolutePath() + File.separatorChar + DiscordChat.modId + File.separatorChar + DiscordChat.modId + ".json");
    // Resulting folder: Minecraft\config\DiscordChat
    @Getter
    private static final File configFolder = configFile.getParentFile();
    @Getter
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    private static Config instance;

    @Getter
    @Expose
    @SerializedName("Enable DiscordChat")
    private boolean enabled = false;
    @Getter
    @Expose
    @SerializedName("Bot Token")
    private String botToken = "";
    @Getter
    @Expose
    @SerializedName("Server ID")
    private String serverId = "";
    @Getter
    @Expose
    @SerializedName("Monitored Channels")
    private String[] monitoredChannels = new String[] { "minecraft", "feed-the-beast", "technic-pack", "forge-server" };

    private static Config loadConfigFile() throws IOException
    {
        // Create config file instance
        Config config = new Config();

        // Check if a config file exists
        if (!configFile.exists())
        {
            if (!configFolder.exists())
            {
                // Create directory structure
                configFolder.mkdirs();
            }

            // Create file within directory
            configFile.createNewFile();
        }

        // Parse the config
        val configJson = FileUtils.readFileToString(configFile, "utf-8");
        val parsedConfig = gson.fromJson(configJson, Config.class);
        if (parsedConfig != null)
        {
            // Apply parsed config object
            config = parsedConfig;
        }

        // Save the config back to file
        FileUtils.writeStringToFile(configFile, gson.toJson(config), "utf-8");

        // Apply instance
        return config;
    }

    public static void reloadConfig() throws IOException
    {
        // Load the config file
        instance = loadConfigFile();
    }

    // Make constructor private to prevent multiple instantiations
    private Config()
    {
    }
}
