package com.hellzing.discordchat.discord;

import com.hellzing.discordchat.DCCommands;
import com.hellzing.discordchat.commands.ICommand;
import com.hellzing.discordchat.utils.MiscUtils;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class MainListener extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if (event.getMessage().getContent().startsWith("!"))
        {
            String msg = event.getMessage().getContent();
            String commandName = msg.substring(1);
            String[] args = {};
            if (commandName.contains(" "))
            {
                commandName = msg.substring(1, msg.indexOf(" "));
                args = msg.substring(msg.indexOf(" ")).split(" ");
            }

            ICommand command = DCCommands.getInstance().getCommand(commandName);
            if (command != null)
            {
                command.doCommand(event.getChannel().getName(), args);
            }
        }
        else if (MiscUtils.shouldUseChannel(event.getChannel()) && !event.getAuthor().isBot())
        {
            MiscUtils.sendMessage(MiscUtils.fromDiscordMessage(event.getMessage()));
        }
    }
}
