package com.hellzing.discordchat;

import com.hellzing.discordchat.commands.Online;
import com.hellzing.discordchat.discord.DiscordThread;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = DiscordChat.modId, name = DiscordChat.modId, version = DiscordChat.version, acceptableRemoteVersions = "*")
public class DiscordChat
{
    public static final String modId = "DiscordChat";
    public static final String version = "2.0.0";

    public static Logger log = LogManager.getLogger(modId);

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
        // Initialize configs
        DCConfig.init(modConfigDirectory);
        DCPrivateProps.init(modConfigDirectory);

        if (DCPrivateProps.setup)
        {
            // Check if mod is enabled
            if (DCConfig.enabled)
            {
                // Register forge event handlers
                ForgeEventHandler feh = new ForgeEventHandler();
                MinecraftForge.EVENT_BUS.register(feh);
                FMLCommonHandler.instance().bus().register(feh);

                // Register commands
                DCCommands.getInstance().registerCommand(new Online());

                // Start Discord wrapper
                log.info("Connecting to the Discord server...");
                DiscordThread.runThread();
            }
        }
        else
        {
            log.warn("Mod not configured! Set 'setup=true' in DiscordChat.private when done!");
        }
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        // Shutdown Discord wrapper
        DiscordThread.instance.jda.shutdown();
    }
}
