package com.hellzing.discordchat;

import com.hellzing.discordchat.commands.Online;
import com.hellzing.discordchat.commands.Reload;
import com.hellzing.discordchat.data.Config;
import com.hellzing.discordchat.data.Messages;
import com.hellzing.discordchat.discord.DiscordWrapper;
import com.hellzing.discordchat.listeners.ForgeListener;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import lombok.Getter;
import lombok.val;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(modid = DiscordChat.modId, name = DiscordChat.modId, version = DiscordChat.version, acceptableRemoteVersions = "*")
public class DiscordChat
{
    public static final String modId = "DiscordChat";
    public static final String version = "2.0.0";

    @Getter
    private static Logger logger = LogManager.getLogger(modId);
    @Getter
    private static java.io.File modConfigDirectory;

    private static boolean initialized = true;

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event)
    {
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

            // Mark as failed to initialize
            initialized = false;
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

        // Send message
        DiscordWrapper.sendMessageToAllChannels("`Server has successfully started and you are ready to join!` :ok_hand:");
    }

    @Mod.EventHandler
    public void onFMLServerStopping(FMLServerStoppingEvent event)
    {
        // Notify channels
        DiscordWrapper.sendMessageToAllChannels("`Server is shutting down!` :sleeping:");

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
}
