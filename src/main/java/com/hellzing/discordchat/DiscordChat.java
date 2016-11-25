package com.hellzing.discordchat;

import com.hellzing.discordchat.commands.Online;
import com.hellzing.discordchat.commands.Reload;
import com.hellzing.discordchat.data.Config;
import com.hellzing.discordchat.data.Messages;
import com.hellzing.discordchat.discord.DiscordWrapper;
import com.hellzing.discordchat.listeners.ForgeListener;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import lombok.Getter;
import lombok.val;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = DiscordChat.modId, name = DiscordChat.modId, version = DiscordChat.version, acceptableRemoteVersions = "*")
public class DiscordChat
{
    public static final String modId = "DiscordChat";
    public static final String version = "2.0.0";

    @Getter
    private static Logger logger = LogManager.getLogger(modId);
    @Getter
    private static java.io.File modConfigDirectory;

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event)
    {
        // Set mod config directory;
        modConfigDirectory = event.getModConfigurationDirectory();
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        try
        {
            // Create data holding classes instances
            Config.getInstance();
            Messages.getInstance();

            // Check if mod is enabled
            if (Config.getInstance().isEnabled())
            {
                // Register forge event handlers
                val eventHandler = new ForgeListener();
                MinecraftForge.EVENT_BUS.register(eventHandler);
                FMLCommonHandler.instance().bus().register(eventHandler);

                // Register commands
                Commands.getInstance().registerCommand(new Online());
                Commands.getInstance().registerCommand(new Reload());

                // Start DiscordWrapper wrapper
                logger.info("Connecting to Discord...");
                DiscordWrapper.initialize();
            }
        }
        catch (Exception e)
        {
            logger.error("An exception occurred while initializing " + modId, e);
        }
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        // Shutdown DiscordWrapper wrapper
        DiscordWrapper.getInstance().getJda().shutdown();
    }
}
