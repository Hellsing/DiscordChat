package com.hellzing.discordchat;

import net.shadowfacts.shadowmc.config.Config;
import net.shadowfacts.shadowmc.config.ConfigManager;
import net.shadowfacts.shadowmc.config.ConfigProperty;

import java.io.File;
import java.util.Arrays;

@Config(name = DiscordChat.modId)
public class DCConfig
{
    @ConfigProperty(comment = "Enable DiscordChat")
    public static boolean enabled = true;

    @ConfigProperty(category = "discord", comment = "The token used to identify your bot to Discord.\nRequired")
    public static String botToken = "";

    @ConfigProperty(category = "discord", comment = "The server ID to connect to.")
    public static String serverId = "";

    @ConfigProperty(category = "discord", comment = "Channels that should be forwarded to MC/MC be forwarded to.\n(Without the # at the beginning)")
    public static String[] channels = new String[0];

    public static void init(File configDir)
    {
        ConfigManager.instance.configDirPath = configDir.getAbsolutePath();
        ConfigManager.instance.register(DiscordChat.modId, DCConfig.class);
        load();
    }

    public static void load()
    {
        ConfigManager.instance.load(DiscordChat.modId);
        if (botToken.isEmpty() || serverId.isEmpty() || Arrays.equals(channels, new String[0]))
        {
            DiscordChat.getLogger().warn("Missing required information, disabling DiscordChat");
            DiscordChat.getLogger().warn("Please go to config/shadowfacts/DiscordChat.cfg and fill out the required fields and restart Minecraft to enable DiscordChat");
            enabled = false;
        }
    }
}
