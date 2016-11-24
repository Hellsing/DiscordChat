package com.hellzing.discordchat;

import com.hellzing.discordchat.discord.DiscordWrapper;
import com.hellzing.discordchat.utils.MessageFormatter;
import com.hellzing.discordchat.utils.Utility;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import lombok.val;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
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
            DiscordWrapper.getInstance().setCurrentGame(gameName);
        }
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event)
    {
        if (event.player != null)
        {
            // Send the chat message to the Discord server
            DiscordWrapper.getInstance().sendMessageToAllChannels(MessageFormatter.getMinecraftToDiscordMessage(event.username, event.message));
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if (event.entityLiving instanceof EntityPlayer)
        {
            // Send the death message to the Discord server
            DiscordWrapper.getInstance()
                    .sendMessageToAllChannels(Utility.stripMinecraftColors(MessageFormatter.getPlayerDeathMessage(event.entityLiving.func_110142_aN().func_151521_b().getUnformattedText())));
        }
    }

    @SubscribeEvent
    public void onAchievement(AchievementEvent event)
    {
        if (event.entityPlayer instanceof EntityPlayerMP)
        {
            // Check if player has the achievement already or can't get it
            val stats = ((EntityPlayerMP) event.entityPlayer).func_147099_x();
            if (stats.hasAchievementUnlocked(event.achievement) || !stats.canUnlockAchievement(event.achievement))
            {
                return;
            }

            // Get achievement text
            IChatComponent achievementText = event.achievement.func_150951_e();

            // Send the achievement message to the Discord server
            DiscordWrapper.getInstance().sendMessageToAllChannels(MessageFormatter.getPlayerAchievementMessage(Utility.getPlayerName(event.entityPlayer), achievementText.getUnformattedText()));
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        // Send the join message to the Discord server
        DiscordWrapper.getInstance().sendMessageToAllChannels(MessageFormatter.getPlayerJoinMessage(Utility.getPlayerName(event.player)));
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        // Send the leave message to the Discord server
        DiscordWrapper.getInstance().sendMessageToAllChannels(MessageFormatter.getPlayerLeaveMessage(Utility.getPlayerName(event.player)));
    }
}
