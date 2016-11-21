package com.hellzing.discordchat;

import com.hellzing.discordchat.discord.DiscordWrapper;
import com.hellzing.discordchat.utils.MiscUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.val;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;

public class ForgeEventHandler
{
    private static final String onlineFormat = "%1$d/%2$d currently";
    private static long lastUpdate;

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event)
    {
        if (System.currentTimeMillis() - lastUpdate > 5000)
        {
            lastUpdate = System.currentTimeMillis();

            // Create new game based on players
            String gameName = "alone...";
            if (MinecraftServer.getServer().getCurrentPlayerCount() > 0)
            {
                gameName = String.format(onlineFormat, MinecraftServer.getServer().getCurrentPlayerCount(), MinecraftServer.getServer().getMaxPlayers());
            }

            // Apply game
            DiscordWrapper.getInstance().jda.getAccountManager().setGame(gameName);
        }
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event)
    {
        if (!MiscUtils.isMessageFromDiscord(event.message))
        {
            DiscordWrapper.getInstance().sendMessageToAllChannels(MiscUtils.toDiscordMessage(event.username, event.message));
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if (DCConfig.sendPlayerDeathMessages && event.entityLiving instanceof EntityPlayer)
        {
            DiscordWrapper.getInstance().sendMessageToAllChannels(MiscUtils.createDiscordDeathMessage((EntityPlayer) event.entityLiving));
        }
    }

    @SubscribeEvent
    public void onAchievement(AchievementEvent event)
    {
        if (DCConfig.sendPlayerAchievementMessages && event.entityPlayer instanceof EntityPlayerMP)
        {
            // Check if player has the achievement already or can't get it
            val playerMP = (EntityPlayerMP) event.entityPlayer;
            if (playerMP.func_147099_x().hasAchievementUnlocked(event.achievement) || !playerMP.func_147099_x().canUnlockAchievement(event.achievement))
            {
                return;
            }

            DiscordWrapper.getInstance().sendMessageToAllChannels(MiscUtils.createAchievementMessage(event.entityPlayer, event.achievement));
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (DCConfig.sendPlayerJoinLeaveMessages)
        {
            DiscordWrapper.getInstance().sendMessageToAllChannels(MiscUtils.createLoggedInMessage(event.player));
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (DCConfig.sendPlayerJoinLeaveMessages)
        {
            DiscordWrapper.getInstance().sendMessageToAllChannels(MiscUtils.createLoggedOutMessage(event.player));
        }
    }
}
