package com.hellzing.discordchat.discord;

import com.hellzing.discordchat.DiscordChat;
import com.hellzing.discordchat.data.Config;
import com.hellzing.discordchat.listeners.DiscordListener;
import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;
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
    private static TemmieWebhook webhook = new TemmieWebhook(Config.getInstance().getWebhookUrl());

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

    public static void shutdown()
    {
        if (discordThread != null)
        {
            instance.jda.shutdown();
        }
    }

    private DiscordWrapper() throws Exception
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
            val server = getServer();
            if (server == null)
            {
                DiscordChat.getLogger().error("Couldn't get the server with the specified ID, please check the config and ensure the ID is correct.");
                return;
            }
            else
            {
                // Mark as ready
                ready = true;

                // Enable auto reconnect
                jda.setAutoReconnect(true);

                // Successfully setup, setting game
                if (currentGame != null)
                {
                    jda.getPresence().setGame(Game.of(currentGame));
                }
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

    /**
     * Sends a message to all monitored channels. This will queue the message to be sent async.
     * @param message The message to send.
     */
    public static void sendMessageToChannel(String message)
    {
        sendMessageToChannel(message, false);
    }

    /**
     * Sends a message to all monitored channels.
     * @param message The message to send.
     * @param waitCompletion Whether it should block the thread until the message was sent or not.
     */
    public static void sendMessageToChannel(String message, boolean waitCompletion)
    {
        sendMessageToChannel(Config.getInstance().getMonitoredChannel(), message, waitCompletion);
    }

    /**
     * Sends a message to a specified channel.
     * @param channelName The specified channel.
     * @param message The message to send.
     */
    public static void sendMessageToChannel(String channelName, String message, boolean waitCompletion)
    {
        try
        {
            // Send the message to the channel
            instance.getChannel(channelName).ifPresent(textChannel ->
                                                       {
                                                           val sendMessage = textChannel.sendMessage(message);
                                                           if (waitCompletion)
                                                           {
                                                               sendMessage.complete();
                                                           }
                                                           else
                                                           {
                                                               sendMessage.queue();
                                                           }
                                                       });
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
        if (gameName != null)
        {
            // Apply game
            instance.currentGame = gameName;
            validateCurrentGame();
        }
    }

    /**
     * Validates the current game and applies the last set game if it was not correctly applied due to session clashing.
     */
    public static void validateCurrentGame()
    {
        try
        {
            if (instance.ready && instance.currentGame != null)
            {
                instance.jda.getPresence().setGame(Game.of(instance.currentGame));
            }
        }
        catch (Exception e)
        {
            DiscordChat.getLogger().error("An error occurred while trying to validate the active game: " + instance.currentGame, e);
        }
    }

    /**
     * Returns the handled main Guild handled by this mod.
     * @return The main Guild instance.
     */
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

    /**
     * Returns the server owner of the main Guild.
     * @return The server owner Member instance.
     */
    public static Member getServerOwner()
    {
        return getServer().getOwner();
    }

    /**
     * Returns all Members of the main Guild with admin permissions.
     * @return The matched Members.
     */
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

    public static void sendWebhookMessage(Member fromMember, String message)
    {
        sendWebhookMessage(fromMember.getEffectiveName(), message, fromMember.getUser().getEffectiveAvatarUrl());
    }

    public static void sendWebhookMessage(String displayName, String message, String avatarUrl)
    {
        // Username, Content, Avatar URL
        DiscordMessage dm = new DiscordMessage(displayName + " (Minecraft)", message, avatarUrl != null ? avatarUrl : instance.jda.getSelfUser().getEffectiveAvatarUrl());
        webhook.sendMessage(dm);
    }
}
