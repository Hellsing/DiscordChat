package com.hellzing.discordchat.listeners;

import com.hellzing.discordchat.Commands;
import com.hellzing.discordchat.utils.MessageFormatter;
import com.hellzing.discordchat.utils.Utility;
import lombok.val;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;

public class DiscordListener extends ListenerAdapter
{
    private static final String commandPrefix = "!";
    private static final String blankSpace = " ";

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        // Only handle monitored channels
        if (!event.getAuthor().isBot() && Utility.isChannelMonitored(event.getChannel()))
        {
            // Handle commands
            if (event.getMessage().getContent().startsWith(commandPrefix))
            {
                val msg = event.getMessage().getContent();
                String commandName = msg.substring(1);
                String[] args = {};
                if (commandName.contains(blankSpace))
                {
                    commandName = msg.substring(1, msg.indexOf(blankSpace));
                    args = msg.substring(msg.indexOf(blankSpace)).split(blankSpace);
                }

                val command = Commands.getInstance().getCommand(commandName);
                if (command != null)
                {
                    // Execute command
                    command.doCommand(event.getAuthor(), event.getChannel().getName(), args);

                    // Return code
                    return;
                }
            }

            // Check if there are players online
            if (MinecraftServer.getServer().getCurrentPlayerCount() > 0)
            {
                // Send the message to the Minecraft server (without color codes)
                Utility.sendMinecraftChat(MessageFormatter.getDiscordToMinecraftMessage(event.getMessage().getAuthor().getUsername(), Utility.stripMinecraftColors(event.getMessage().getContent())));

            }
        }
    }
}
