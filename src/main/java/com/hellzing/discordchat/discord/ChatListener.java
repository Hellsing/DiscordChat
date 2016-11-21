package com.hellzing.discordchat.discord;

import com.hellzing.discordchat.DCCommands;
import com.hellzing.discordchat.utils.MiscUtils;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import lombok.val;

public class ChatListener extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        // Only handle monitored channels
        if (!event.getAuthor().isBot() && MiscUtils.shouldUseChannel(event.getChannel()))
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
                    command.doCommand(event.getAuthor(), event.getChannel().getName(), args);
                }
            }
            // Handle regular messages
            else
            {
                MiscUtils.sendMessage(MiscUtils.fromDiscordMessage(event.getMessage()));
            }
        }
    }
}
