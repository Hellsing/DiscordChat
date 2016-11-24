package com.hellzing.discordchat.discord;

import com.hellzing.discordchat.data.Config;
import com.hellzing.discordchat.DiscordChat;
import com.hellzing.discordchat.listeners.DiscordListener;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.util.Optional;

public class DiscordWrapper implements Runnable
{
    private static Thread discordThread;

    @Getter
    private static DiscordWrapper instance;

    @Getter
    private JDA jda;
    @Getter
    private String currentGame;

    public static void initialize() throws Exception
    {
        if (discordThread == null)
        {
            discordThread = new Thread(new DiscordWrapper());
            discordThread.run();
        }
        else
        {
            DiscordChat.getLogger().error("JDA thread is already running!");
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
            jda = new JDABuilder().setBotToken(Config.getInstance().getBotToken()).addListener(new DiscordListener()).buildBlocking();

            // Get handled server
            val server = jda.getGuildById(Config.getInstance().getServerId());
            if (server == null)
            {
                DiscordChat.getLogger().error("Couldn't get the server with the specified ID, please check the config and ensure the ID is correct.");
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
            DiscordChat.getLogger().error("Invalid login credentials for DiscordWrapper, disabling DiscordChat");
            e.printStackTrace();
            return;
        }
        catch (InterruptedException e)
        {
            DiscordChat.getLogger().error("Couldn't complete login, disabling DiscordChat");
            e.printStackTrace();
            return;
        }
    }

    public void sendMessageToAllChannels(String message)
    {
        for (val channelName : Config.getInstance().getMonitoredChannels())
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
            DiscordChat.getLogger().warn("Discord chat rate limit exceeded, try again in " + e.getAvailTime() + "ms");
        }
        catch (Exception e)
        {
            DiscordChat.getLogger().error("An error occurred while trying to send a text message to a discord channel.\r\nChannel name: " + channelName + " | Message:\r\n" + message, e);
        }
    }

    /**
     * Applies a custom name as currently playing game.
     * @param gameName The desired game name.
     */
    public void setCurrentGame(String gameName)
    {
        if (currentGame == null || !currentGame.equals(gameName))
        {
            try
            {
                // Apply game
                currentGame = gameName;
                jda.getAccountManager().setGame(gameName);
            }
            catch (Exception e)
            {
                DiscordChat.getLogger().error("An error occurred while trying to set a new active game: " + gameName, e);
            }
        }
    }

    private Optional<TextChannel> getChannel(String channelName)
    {
        return jda.getGuildById(Config.getInstance().getServerId()).getTextChannels().stream().filter(channel -> channel.getName().equalsIgnoreCase(channelName)).findFirst();
    }
}
