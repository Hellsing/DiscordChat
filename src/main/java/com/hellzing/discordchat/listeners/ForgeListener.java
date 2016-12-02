package com.hellzing.discordchat.listeners;

import com.hellzing.discordchat.data.Messages;
import com.hellzing.discordchat.discord.DiscordWrapper;
import com.hellzing.discordchat.utils.MessageFormatter;
import com.hellzing.discordchat.utils.Utility;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.val;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;

public class ForgeListener
{
    private static final String onlineFormat = "%1$d/%2$d currently";
    private static long lastUpdate;

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event)
    {
        if (System.currentTimeMillis() - lastUpdate > 1000)
        {
            lastUpdate = System.currentTimeMillis();

            // Create new game based on players
            String gameName = "alone...";
            if (MinecraftServer.getServer().getCurrentPlayerCount() > 0)
            {
                gameName = String.format(onlineFormat, MinecraftServer.getServer().getCurrentPlayerCount(), MinecraftServer.getServer().getMaxPlayers());
            }

            // Apply the custom game
            DiscordWrapper.setCurrentGame(gameName);
        }
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event)
    {
        if (event.player != null && Messages.getInstance().getDiscord().getMinecraftChat().isEnabled())
        {
            // Send the chat message to the Discord server
            DiscordWrapper.sendMessageToAllChannels(MessageFormatter.getMinecraftToDiscordMessage(event.username, event.message));
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        // Player death
        if (event.entityLiving instanceof EntityPlayer && Messages.getInstance().getDiscord().getPlayerDeath().isEnabled())
        {
            // Send the death message to the Discord server
            DiscordWrapper.sendMessageToAllChannels(Utility.stripMinecraftColors(MessageFormatter.getPlayerDeathMessage(event.entityLiving.getCombatTracker().func_151521_b().getUnformattedText())));
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
            DiscordWrapper.sendMessageToAllChannels(Utility.stripMinecraftColors(MessageFormatter.getPlayerBossKilledMessage(dimensionName, playerName, bossName)));
        }
    }

    @SubscribeEvent
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
            DiscordWrapper.sendMessageToAllChannels(MessageFormatter.getPlayerAchievementMessage(Utility.getPlayerName(event.entityPlayer), achievementText.getUnformattedText()));
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (Messages.getInstance().getDiscord().getPlayerJoin().isEnabled())
        {
            // Send the join message to the Discord server
            DiscordWrapper.sendMessageToAllChannels(MessageFormatter.getPlayerJoinMessage(Utility.getPlayerName(event.player)));
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (Messages.getInstance().getDiscord().getPlayerLeave().isEnabled())
        {
            // Send the leave message to the Discord server
            DiscordWrapper.sendMessageToAllChannels(MessageFormatter.getPlayerLeaveMessage(Utility.getPlayerName(event.player)));
        }
    }
}
