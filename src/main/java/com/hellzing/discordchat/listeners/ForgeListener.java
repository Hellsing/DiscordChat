package com.hellzing.discordchat.listeners;

import com.hellzing.discordchat.data.Messages;
import com.hellzing.discordchat.data.Users;
import com.hellzing.discordchat.discord.DiscordWrapper;
import com.hellzing.discordchat.utils.MessageFormatter;
import com.hellzing.discordchat.utils.Utility;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.val;
import net.dv8tion.jda.core.entities.Member;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;

public class ForgeListener
{
    private static final String onlineFormat = "-> %1$d/%2$d";
    private static long lastUpdate;

    /**
     * Updates the player count displayed on Discord as currently played game.
     */
    private void updatePlayerCountDiscord()
    {
        // Create new game based on players
        String gameName = "alone :^(";
        if (MinecraftServer.getServer().getCurrentPlayerCount() > 0)
        {
            gameName = String.format(onlineFormat, MinecraftServer.getServer().getCurrentPlayerCount(), MinecraftServer.getServer().getMaxPlayers());
        }

        // Apply the custom game
        DiscordWrapper.setCurrentGame(gameName);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event)
    {
        if (System.currentTimeMillis() - lastUpdate > 2500)
        {
            lastUpdate = System.currentTimeMillis();
            updatePlayerCountDiscord();
        }
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event)
    {
        // Send message to Discord Channel
        if (event.player != null && Messages.getInstance().getDiscord().getMinecraftChat().isEnabled())
        {
            // Parse the message to be sent to the server
            val message = MessageFormatter.getMinecraftToDiscordMessage(event.message);

            Member discordMember = null;
            if (Users.getInstance().getLinkedUsers().containsKey(event.username))
            {
                discordMember = DiscordWrapper.getServer().getMemberById(Users.getInstance().getLinkedUsers().get(event.username));
            }

            if (discordMember != null)
            {
                DiscordWrapper.sendWebhookMessage(discordMember, message);
            }
            else
            {
                DiscordWrapper.sendWebhookMessage(event.username, message, null);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDeath(LivingDeathEvent event)
    {
        // Player death
        if (event.entityLiving instanceof EntityPlayer && Messages.getInstance().getDiscord().getPlayerDeath().isEnabled())
        {
            // Send the death message to the Discord server
            DiscordWrapper.sendMessageToChannel(Utility.stripMinecraftColors(MessageFormatter.getPlayerDeathMessage(event.entityLiving.getCombatTracker().func_151521_b().getUnformattedText())));
        }

        // Boss killed
        if (event.entityLiving instanceof IBossDisplayData && event.source.getEntity() instanceof EntityPlayer && Messages.getInstance().getDiscord().getPlayerBossKilled().isEnabled())
        {
            // Get the dimension name
            val dimensionName = event.entity.worldObj.provider.getDimensionName();

            // Get the killing player name
            val playerName = Utility.getPlayerName((EntityPlayer) event.source.getEntity());

            // Get boss name
            val bossName = event.entityLiving.getFormattedCommandSenderName().getUnformattedText();

            // Send the boss killed message to the Discord server
            DiscordWrapper.sendMessageToChannel(Utility.stripMinecraftColors(MessageFormatter.getPlayerBossKilledMessage(dimensionName, playerName, bossName)));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAchievement(AchievementEvent event)
    {
        if (event.entityPlayer instanceof EntityPlayerMP && Messages.getInstance().getDiscord().getPlayerAchievement().isEnabled())
        {
            // Check if player has the achievement already or can't get it
            val stats = ((EntityPlayerMP) event.entityPlayer).getStatFile();
            if (stats.hasAchievementUnlocked(event.achievement) || !stats.canUnlockAchievement(event.achievement))
            {
                return;
            }

            // Get achievement text
            val achievementText = event.achievement.getStatName();

            // Send the achievement message to the Discord server
            DiscordWrapper.sendMessageToChannel(MessageFormatter.getPlayerAchievementMessage(Utility.getPlayerName(event.entityPlayer), achievementText.getUnformattedText()));
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (Messages.getInstance().getDiscord().getPlayerJoin().isEnabled())
        {
            // Send the join message to the Discord server
            DiscordWrapper.sendMessageToChannel(MessageFormatter.getPlayerJoinMessage(Utility.getPlayerName(event.player)));

            // Update player count
            updatePlayerCountDiscord();

        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (Messages.getInstance().getDiscord().getPlayerLeave().isEnabled())
        {
            // Send the leave message to the Discord server
            DiscordWrapper.sendMessageToChannel(MessageFormatter.getPlayerLeaveMessage(Utility.getPlayerName(event.player)));

            // Update player count
            updatePlayerCountDiscord();
        }
    }
}
