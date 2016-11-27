package com.hellzing.discordchat.discord;

import com.hellzing.discordchat.DiscordChat;
import com.hellzing.discordchat.data.Config;
import com.hellzing.discordchat.listeners.DiscordListener;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class DiscordWrapper implements Runnable
{
    private static Thread discordThread;

    @Getter
    private static DiscordWrapper instance;

    @Getter
    private JDA jda;
    @Getter
    private boolean ready;
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
            jda = new JDABuilder(AccountType.BOT).setToken(Config.getInstance().getBotToken()).addListener(new DiscordListener()).buildBlocking();

            // Get handled server
            val server = jda.getGuildById(Config.getInstance().getServerId());
            if (server == null)
            {
                DiscordChat.getLogger().error("Couldn't get the server with the specified ID, please check the config and ensure the ID is correct.");
                return;
            }
            else
            {
                // Mark as ready
                ready = true;

                // Successfully setup, setting game
                jda.getPresence().setGame(Game.of(currentGame));
            }
        }
        catch (LoginException | IllegalArgumentException e)
        {
            DiscordChat.getLogger().error("An error occurred, invalid login credentials for Discord!", e);
        }
        catch (InterruptedException e)
        {
            DiscordChat.getLogger().error("An error occurred, could not complete login!", e);
        }
        catch (Exception e)
        {
            DiscordChat.getLogger().error("An unknown error occurred!", e);
        }
    }

    public static void sendMessageToAllChannels(String message)
    {
        for (val channelName : Config.getInstance().getMonitoredChannels())
        {
            sendMessageToChannel(channelName, message);
        }
    }

    public static void sendMessageToChannel(String channelName, String message)
    {
        try
        {
            val channel = instance.getChannel(channelName);
            if (channel.isPresent())
            {
                channel.get().sendMessage(message);
            }
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
    public static void setCurrentGame(String gameName)
    {
        try
        {
            if (instance.currentGame == null || !instance.jda.getPresence().getGame().getName().equals(gameName))
            {

                // Apply game
                instance.currentGame = gameName;
                if (instance.ready)
                {
                    instance.jda.getPresence().setGame(Game.of(gameName));
                }
            }
        }
        catch (Exception e)
        {
            DiscordChat.getLogger().error("An error occurred while trying to set a new active game: " + gameName, e);
        }
    }

    public static Guild getServer()
    {
        return instance.jda.getGuildById(Config.getInstance().getServerId());
    }

    private Optional<TextChannel> getChannel(String channelName)
    {
        val channels = getServer().getTextChannelsByName(channelName, true);
        if (channels != null && channels.size() > 0)
        {
            // Return first channel found with that name
            return Optional.of(channels.get(0));
        }

        // No channel found
        return Optional.empty();
    }

    public static Member getServerOwner()
    {
        return instance.jda.getGuildById(Config.getInstance().getServerId()).getOwner();
    }

    public static List<Member> getServerAdmins()
    {
        val admins = new HashSet<Member>();

        for (val role : getServer().getRoles())
        {
            if (role.getPermissions().stream().anyMatch(perm -> perm.name().toLowerCase().contains("admin")))
            {
                for (val user : getServer().getMembersWithRoles(role))
                {
                    admins.add(user);
                }
            }
        }

        return new ArrayList<>(admins);
    }
}
