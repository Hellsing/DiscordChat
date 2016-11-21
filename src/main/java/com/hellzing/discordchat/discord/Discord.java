package com.hellzing.discordchat.discord;

import com.hellzing.discordchat.DCConfig;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import com.hellzing.discordchat.DiscordChat;

import javax.security.auth.login.LoginException;
import java.util.Optional;

public class Discord implements Runnable
{
    private static Thread thread;

    public static Discord instance;

    public JDA jda;

    public static void initialize()
    {
        if (thread == null)
        {
            thread = new Thread(new Discord());
            thread.run();
        }
        else
        {
            DiscordChat.log.error("JDA thread is already running!");
        }
    }

    public Discord()
    {
        instance = this;
    }

    @Override
    public void run()
    {
        try
        {
            jda = new JDABuilder().setBotToken(DCConfig.botToken).addListener(new ChatListener()).buildBlocking();
        }
        catch (LoginException | IllegalArgumentException e)
        {
            DiscordChat.log.error("Invalid login credentials for Discord, disabling DiscordChat");
            e.printStackTrace();
            DCConfig.enabled = false;
            return;
        }
        catch (InterruptedException e)
        {
            DiscordChat.log.error("Couldn't complete login, disabling DiscordChat");
            e.printStackTrace();
            DCConfig.enabled = false;
            return;
        }
        if (jda != null)
        {
            Guild server = jda.getGuildById(DCConfig.serverId);
            if (server == null)
            {
                DiscordChat.log.error("Couldn't get the server with the specified ID, please check the config and ensure the ID is correct.");
                DCConfig.enabled = false;
                return;
            }
            else
            {
                // Set temporary game
                jda.getAccountManager().setGame("initializing...");
            }
        }
    }

    public void sendMessageToAllChannels(String message)
    {
        for (String name : DCConfig.channels)
        {
            Optional<TextChannel> channel = getChannel(name);
            if (channel.isPresent())
            {
                channel.get().sendMessage(message);
            }
        }
    }

    public void sendMessageToChannel(String channelName, String message)
    {
        Optional<TextChannel> channel = getChannel(channelName);
        if (channel.isPresent())
        {
            channel.get().sendMessage(message);
        }
    }

    private Optional<TextChannel> getChannel(String channelName)
    {
        return jda.getGuildById(DCConfig.serverId).getTextChannels().stream().filter(channel -> channel.getName().equalsIgnoreCase(channelName)).findFirst();
    }
}
