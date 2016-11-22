package com.hellzing.discordchat.discord;

import com.hellzing.discordchat.DCCommands;
import com.hellzing.discordchat.utils.MessageFormatter;
import com.hellzing.discordchat.utils.Utility;
import lombok.val;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class ChatListener extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        // Only handle monitored channels
        if (!event.getAuthor().isBot() && Utility.isChannelMonitored(event.getChannel()))
        {
            // Handle commands
            if (event.getMessage().getContent().startsWith("!"))
            {
                val msg = event.getMessage().getContent();
                String commandName = msg.substring(1);
                String[] args = {};
                if (commandName.contains(" "))
                {
                    commandName = msg.substring(1, msg.indexOf(" "));
                    args = msg.substring(msg.indexOf(" ")).split(" ");
                }

                val command = DCCommands.getInstance().getCommand(commandName);
                if (command != null)
                {
                    // Execute command
                    command.doCommand(event.getAuthor(), event.getChannel().getName(), args);

                    // Return code
                    return;
                }
            }

            // Handle regular messages
            Utility.sendMinecraftChat(MessageFormatter.getDiscordToMinecraftMessage(event.getMessage().getAuthor().getUsername(), event.getMessage().getContent()));
        }
    }
}
