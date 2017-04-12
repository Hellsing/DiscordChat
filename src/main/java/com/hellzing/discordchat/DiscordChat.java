package com.hellzing.discordchat;

import com.hellzing.discordchat.commands.LinkUser;
import com.hellzing.discordchat.commands.Online;
import com.hellzing.discordchat.commands.Reload;
import com.hellzing.discordchat.data.Config;
import com.hellzing.discordchat.data.Messages;
import com.hellzing.discordchat.data.Users;
import com.hellzing.discordchat.discord.DiscordWrapper;
import com.hellzing.discordchat.listeners.ForgeListener;
import com.hellzing.discordchat.utils.Utility;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import lombok.Getter;
import lombok.val;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = DiscordChat.modId, name = DiscordChat.modId, version = DiscordChat.version, acceptableRemoteVersions = "*")
public class DiscordChat
{
    public static final String modId = "DiscordChat";
    public static final String version = "2.0.0";

    @Getter
    private static Logger logger;
    @Getter
    private static java.io.File modConfigDirectory;

    @Getter
    private static Map<String, String> userLinkMap = new HashMap<>();

    private static boolean initialized;

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event)
    {
        // Apply logger
        logger = event.getModLog();

        // Set mod config directory;
        modConfigDirectory = event.getModConfigurationDirectory();

        try
        {
            // Load data related files
            reloadConfigs();

            try
            {
                // Check if mod is enabled
                if (Config.getInstance().isEnabled())
                {
                    // Start DiscordWrapper wrapper
                    DiscordWrapper.initialize();

                    // Set Discord status
                    DiscordWrapper.setCurrentGame("PreInit (1/4)");

                    // Mark as initialized
                    initialized = true;
                }
            }
            catch (Exception e)
            {
                logger.error("An exception occurred while initializing", e);
            }
        }
        catch (IOException e)
        {
            logger.error("An exception occurred while loading the config files", e);
        }
    }

    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event)
    {
        // Check if initialization was successful and the mod is enabled
        if (!isEnabled())
        {
            return;
        }

        // Set Discord status
        DiscordWrapper.setCurrentGame("Init (2/4)");
    }

    @Mod.EventHandler
    public void onFMLPostInitialization(FMLPostInitializationEvent event)
    {
        // Check if initialization was successful and the mod is enabled
        if (!isEnabled())
        {
            return;
        }

        // Set Discord status
        DiscordWrapper.setCurrentGame("PostInit (3/4)");
    }

    @Mod.EventHandler
    public void onFMLServerStarting(FMLServerStartingEvent event)
    {
        // Check if initialization was successful and the mod is enabled
        if (!isEnabled())
        {
            return;
        }

        // Set Discord status
        DiscordWrapper.setCurrentGame("Starting (4/4)");

        try
        {
            // Register forge event handlers
            val eventHandler = new ForgeListener();
            MinecraftForge.EVENT_BUS.register(eventHandler);
            FMLCommonHandler.instance().bus().register(eventHandler);
        }
        catch (Exception e)
        {
            logger.error("An exception occurred while registering event handlers", e);
        }

        try
        {
            // Register commands
            Commands.getInstance().registerCommand(new Online());
            Commands.getInstance().registerCommand(new Reload());
            Commands.getInstance().registerCommand(new LinkUser());
        }
        catch (Exception e)
        {
            logger.error("An exception occurred while registering commands", e);
        }
    }

    @Mod.EventHandler
    public void onFMLServerStarted(FMLServerStartedEvent event)
    {
        // Check if initialization was successful and the mod is enabled
        if (!isEnabled())
        {
            return;
        }

        if (DiscordWrapper.getInstance().isReady())
        {
            Utility.validateMonitoredChannels();
        }

        if (Messages.getInstance().getDiscord().getServerStarted().isEnabled())
        {
            // Send message
            DiscordWrapper.sendMessageToChannel(Messages.getInstance().getDiscord().getServerStarted().getMessageFormat());
        }
    }

    @Mod.EventHandler
    public void onFMLServerStopping(FMLServerStoppingEvent event)
    {
        if (Messages.getInstance().getDiscord().getServerStopping().isEnabled())
        {
            // Notify channels
            DiscordWrapper.sendMessageToChannel(Messages.getInstance().getDiscord().getServerStopping().getMessageFormat());
        }

        // Shutdown DiscordWrapper wrapper
        DiscordWrapper.getInstance().getJda().shutdown();
    }

    /**
     * Reloads all data related files, such as the main configuration file.
     */
    public static void reloadConfigs() throws IOException
    {
        Config.reloadConfig();
        Messages.reloadConfig();
        Users.reloadConfig();

        if (DiscordWrapper.getInstance() != null && DiscordWrapper.getInstance().isReady())
        {
            Utility.validateMonitoredChannels();
        }
    }

    /**
     * Checks if the initialization was successful and the mod is enabled by config.
     * @return Whether the mod is enabled or not.
     */
    public static boolean isEnabled()
    {
        // Check if initialization was successful and the mod is enabled
        return initialized && Config.getInstance().isEnabled();
    }

    /**
     * Returns an input stream for reading the specified resource.
     * @param resource The resource name.
     * @return An input stream for reading the resource, or null if resource could not be found.
     */
    public static InputStream getResourceAsStream(String resource)
    {
        return DiscordChat.class.getClassLoader().getResourceAsStream(resource);
    }
}
