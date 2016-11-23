package com.hellzing.discordchat.discord;

import com.hellzing.discordchat.DCConfig;
import com.hellzing.discordchat.DiscordChat;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.exceptions.RateLimitedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.Optional;

public class DiscordWrapper implements Runnable
{
    private static Thread thread;

    @Getter
    private static DiscordWrapper instance;

    public static Logger log = LogManager.getLogger(DiscordChat.modId);

    public JDA jda;

    public static void initialize() throws Exception
    {
        if (thread == null)
        {
            thread = new Thread(new DiscordWrapper());
            thread.run();
        }
        else
        {
            DiscordChat.log.error("JDA thread is already running!");
        }
    }

    public DiscordWrapper() throws Exception
    {
        if (instance != null)
        {
            throw new Exception("Already initialized DiscordWrapper!");
        }
        instance = this;
    }

    @Override
    public void run()
    {
        try
        {
            // Build up DiscordWrapper connection
            jda = new JDABuilder().setBotToken(DCConfig.botToken).addListener(new ChatListener()).buildBlocking();

            // Get handled server
            val server = jda.getGuildById(DCConfig.serverId);
            if (server == null)
            {
                DiscordChat.log.error("Couldn't get the server with the specified ID, please check the config and ensure the ID is correct.");
                DCConfig.enabled = false;
                return;
            }
            else
            {
                // Successfully setup, setting temporary game
                jda.getAccountManager().setGame("initializing...");
            }
        }
        catch (LoginException | IllegalArgumentException e)
        {
            DiscordChat.log.error("Invalid login credentials for DiscordWrapper, disabling DiscordChat");
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
    }

    public void sendMessageToAllChannels(String message)
    {
        for (val channelName : DCConfig.channels)
        {
            sendMessageToChannel(channelName, message);
        }
    }

    public void sendMessageToChannel(String channelName, String message)
    {
        try
        {
            val channel = getChannel(channelName);
            if (channel.isPresent())
            {
                channel.get().sendMessage(message);
            }
        }
        catch (RateLimitedException e)
        {
            log.warn("Discord chat rate limit exceeded, try again in " + e.getAvailTime() + "ms");
        }
        catch (Exception e)
        {
            log.warn("An error occurred while trying to send a text message to a discord channel named: " + channelName);
            log.warn("Message content:\r\n" + message);
            log.warn("Exception stacktrace:");
            e.printStackTrace();
        }
    }

    private Optional<TextChannel> getChannel(String channelName)
    {
        return jda.getGuildById(DCConfig.serverId).getTextChannels().stream().filter(channel -> channel.getName().equalsIgnoreCase(channelName)).findFirst();
    }
}
