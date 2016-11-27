package com.hellzing.discordchat.listeners;

import com.hellzing.discordchat.Commands;
import com.hellzing.discordchat.DiscordChat;
import com.hellzing.discordchat.commands.ICommand;
import com.hellzing.discordchat.discord.DiscordWrapper;
import com.hellzing.discordchat.utils.MessageFormatter;
import com.hellzing.discordchat.utils.Utility;
import lombok.val;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;

public class DiscordListener extends ListenerAdapter
{
    private static final String commandPrefix = "!";
    private static final String blankSpace = " ";

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        try
        {
            // Ignore bot messages
            if (event.getAuthor().isBot())
            {
                return;
            }

            // Ignore non-monitored channels
            if (!event.isFromType(ChannelType.PRIVATE) && !Utility.isChannelMonitored(event.getTextChannel()))
            {
                return;
            }

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
                    // Check command type
                    if (command.getChannelType() == ICommand.ChannelType.BOTH
                            || command.getChannelType() == ICommand.ChannelType.PRIVATE && event.isFromType(ChannelType.PRIVATE)
                            || command.getChannelType() == ICommand.ChannelType.GUILD && !event.isFromType(ChannelType.PRIVATE))
                    {
                        if (command.getPermissionType() == ICommand.PermissionType.EVERYONE
                                || (command.getPermissionType() == ICommand.PermissionType.OWNER
                                || command.getPermissionType() == ICommand.PermissionType.ADMIN) && event.getAuthor().equals(DiscordWrapper.getServerOwner())
                                || command.getPermissionType() == ICommand.PermissionType.ADMIN && DiscordWrapper.getServerAdmins().contains(event.getAuthor()))
                        {
                            // Execute command
                            val result = command.execute(event.getAuthor(), event.getChannel(), args);

                            // Return method if result was true
                            if (result)
                            {
                                return;
                            }
                        }
                        else
                        {
                            // Announce permission error
                            event.getChannel().sendMessage(MessageFormatter.getDiscordCodeBlock("", "Error: You don't have the required permission to run this command!")).queue();
                        }
                    }
                    else
                    {
                        if (event.isFromType(ChannelType.PRIVATE))
                        {
                            // Notify our private chat friend
                            event.getChannel().sendMessage(MessageFormatter.getDiscordCodeBlock("", "Error: This command does not work in this channel!")).queue();
                        }
                    }
                }
                else if (event.isFromType(ChannelType.PRIVATE))
                {
                    // Respond to unknown commands in private chats
                    event.getChannel().sendMessage("I do not recognize this command, sorry!").queue();
                }
            }

            // Handle public messages
            if (!event.isFromType(ChannelType.PRIVATE))
            {
                // Check if there are players online
                if (MinecraftServer.getServer().getCurrentPlayerCount() > 0)
                {
                    // Send the message to the Minecraft server (without color codes)
                    Utility.sendMinecraftChat(MessageFormatter.getDiscordToMinecraftMessage(event.getMessage().getAuthor().getName(),
                                                                                            Utility.Emoji.replaceEmojiWithName(Utility.stripMinecraftColors(event.getMessage().getContent()))));
                }
            }
        }
        catch (Exception e)
        {
            DiscordChat.getLogger().error("Error while trying to handle received message, content: " + event.getMessage().getContent(), e);
        }
    }
}
