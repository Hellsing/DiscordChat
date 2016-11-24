package com.hellzing.discordchat;

import com.hellzing.discordchat.commands.Commands;
import com.hellzing.discordchat.commands.Online;
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
        // Initialize setup config
        DCPrivateProps.init(modConfigDirectory);
        if (DCPrivateProps.setup)
        {
            // Initialize main config
            DCConfig.init(modConfigDirectory);

            // Check if mod is enabled
            if (DCConfig.enabled)
            {
                // Register forge event handlers
                val eventHandler = new ForgeListener();
                MinecraftForge.EVENT_BUS.register(eventHandler);
                FMLCommonHandler.instance().bus().register(eventHandler);

                // Register commands
                Commands.getInstance().registerCommand(new Online());

                try
                {
                    // Start DiscordWrapper wrapper
                    logger.info("Connecting to the DiscordWrapper server...");
                    DiscordWrapper.initialize();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            logger.warn("Mod not configured! Set 'setup=true' in DiscordChat.private when done!");
        }
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        // Shutdown DiscordWrapper wrapper
        DiscordWrapper.getInstance().getJda().shutdown();
    }
}
